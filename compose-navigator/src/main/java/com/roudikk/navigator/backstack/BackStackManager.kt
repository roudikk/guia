package com.roudikk.navigator.backstack

import android.app.Application
import android.os.Bundle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.saveable.SaveableStateHolder
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.LocalSavedStateRegistryOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.savedstate.SavedStateRegistry
import com.roudikk.navigator.core.BackStackEntry
import com.roudikk.navigator.core.BottomSheet
import com.roudikk.navigator.core.Dialog
import com.roudikk.navigator.core.Navigator
import com.roudikk.navigator.core.Screen
import com.roudikk.navigator.core.navigationNode
import com.roudikk.navigator.savedstate.backStackManagerSaver
import java.util.UUID

/**
 * Creates an instance a saveable instance of a [BackStackManager].
 */
@Composable
internal fun rememberBackStackManager(navigator: Navigator): BackStackManager {
    val application = LocalContext.current.applicationContext as Application
    val viewModelStoreOwner = requireNotNull(LocalViewModelStoreOwner.current)
    val savedStateRegistry = LocalSavedStateRegistryOwner.current.savedStateRegistry
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val saveableStateHolder = rememberSaveableStateHolder()

    return rememberSaveable(
        saver = backStackManagerSaver(
            navigator = navigator,
            application = application,
            viewModelStoreOwner = viewModelStoreOwner,
            savedStateRegistry = savedStateRegistry,
            lifecycle = lifecycle,
            saveableStateHolder = saveableStateHolder
        )
    ) {
        BackStackManager(
            id = UUID.randomUUID().toString(),
            initialEntryIds = emptyList(),
            navigator = navigator,
            application = application,
            viewModelStoreOwner = viewModelStoreOwner,
            saveableStateHolder = saveableStateHolder,
            hostLifecycle = lifecycle,
            savedStateRegistry = savedStateRegistry
        )
    }
}

/**
 * Manages the BackStack state of a [Navigator].
 *
 * That includes managing the Lifecycle of each [LifeCycleEntry].
 * All entries in the current [Navigator]'s back stack will have a corresponding [LifeCycleEntry].
 */
internal class BackStackManager(
    internal val id: String,
    private val navigator: Navigator,
    private val application: Application?,
    private val savedStateRegistry: SavedStateRegistry,
    private val saveableStateHolder: SaveableStateHolder,
    private val hostLifecycle: Lifecycle,
    viewModelStoreOwner: ViewModelStoreOwner,
    initialEntryIds: List<String>,
) {
    private var hostLifecycleState: Lifecycle.State = Lifecycle.State.INITIALIZED
    private val lifeCycleEntries = mutableMapOf<String, LifeCycleEntry>()
    internal val entryIds get() = lifeCycleEntries.keys

    private val lifecycleEventObserver = LifecycleEventObserver { _, event ->
        // Update all entries with the current host life cycle state.
        hostLifecycleState = event.targetState
        lifeCycleEntries.values.forEach {
            it.navHostLifecycleState = event.targetState
        }
    }

    private val viewModelStoreProvider: BackStackViewModel = ViewModelProvider(
        viewModelStoreOwner
    )["back-stack-manager-$id", BackStackViewModel::class.java]

    private val backstackIds by derivedStateOf {
        navigator.backStack.map { it.id }
    }

    val visibleBackStack = derivedStateOf {
        val entries = navigator.backStack
        val currentEntry = entries.last()

        // Check if there's a valid screen that should be visible.
        // It's the last entry that is a screen
        val screenEntry = entries.lastOrNull {
            navigator.navigationNode(it) is Screen
        }?.let(::createLifeCycleEntry)

        // Check if there's a valid dialog that should be visible.
        // It must be the last entry in the backstack
        val dialogEntry = currentEntry.takeIf {
            navigator.navigationNode(it) is Dialog
        }?.let(::createLifeCycleEntry)

        // Check if there's a valid bottom sheet that should be visible.
        // It's either the top most destination, in between a screen and a dialog
        // or just by itself
        val bottomSheetEntry = entries
            .lastOrNull { navigator.navigationNode(it) is BottomSheet }
            .takeIf {
                if (currentEntry == it) return@takeIf true
                val bottomSheetIndex = entries.indexOf(it)
                val entriesAfter = entries.subList(bottomSheetIndex + 1, entries.size)
                entriesAfter.all { entry -> navigator.navigationNode(entry) is Dialog }
            }?.let(::createLifeCycleEntry)

        val visibleBackStack = VisibleBackStack(
            screenEntry = screenEntry,
            dialogEntry = dialogEntry,
            bottomSheetEntry = bottomSheetEntry
        )

        // Any entries that are not in the visible stack must now be destroyed.
        lifeCycleEntries.values
            .filter { it !in visibleBackStack.entries }
            .forEach { it.maxLifecycleState = minOf(it.maxLifecycleState, Lifecycle.State.STARTED) }

        val navigatingToDialog = navigator.navigationNode(currentEntry) is Dialog &&
            entries.getOrNull(entries.lastIndex - 1)
                ?.let(navigator::navigationNode) !is Dialog

        val navigatingToBottomSheet = navigator.navigationNode(currentEntry) is BottomSheet &&
            entries.getOrNull(entries.lastIndex - 1)
                ?.let(navigator::navigationNode) !is BottomSheet

        visibleBackStack.entries.forEach {
            // If the current destination is a bottom sheet or a dialog
            // we need to pause whatever is behind it. In the case of a dialog
            // we might have a bottom sheet and/or a screen behind it, whereas a bottom sheet
            // would just have a screen behind it.
            if (navigatingToDialog || navigatingToBottomSheet) {
                if (it.id == currentEntry.id) {
                    it.maxLifecycleState = Lifecycle.State.RESUMED
                } else {
                    it.maxLifecycleState = Lifecycle.State.STARTED
                }
            } else {
                it.maxLifecycleState = Lifecycle.State.STARTED
            }
        }

        visibleBackStack
    }

    init {
        hostLifecycle.addObserver(lifecycleEventObserver)

        // Clear components of restored entries
        initialEntryIds
            .filter { it !in backstackIds }
            .forEach(::removeComponents)

        // Create back stack entries for restored navigation entries
        navigator.backStack
            .filter { it.id in initialEntryIds }
            .forEach(::createLifeCycleEntry)

        // Make sure the back stack entries are initialized with the proper lifecycles
        updateLifecycles()
    }

    /**
     * Creates a [LifeCycleEntry] from the given [BackStackEntry].
     *
     * The [SaveableStateHolder] would be the state holder associated with a [Navigator].
     * The [ViewModelStore] is received from [viewModelStoreProvider] created in the back stack manager.l
     */
    private fun createLifeCycleEntry(backStackEntry: BackStackEntry): LifeCycleEntry {
        return lifeCycleEntries.getOrPut(backStackEntry.id) {
            LifeCycleEntry(
                backStackEntry = backStackEntry,
                saveableStateHolder = saveableStateHolder,
                viewModelStore = viewModelStoreProvider.getViewModelStore(backStackEntry.id),
                application = application
            ).also(::initialBackStackState)
        }
    }

    /**
     * Initializes a [LifeCycleEntry] with the proper state.
     *
     * First, we make sure the [hostLifecycleState] is not restored before restoring the saved state.
     * Then, we update the lifecycle given the host lifecycle and [Lifecycle.State.STARTED].
     */
    private fun initialBackStackState(backStackLifecycleOwner: LifeCycleEntry) {
        if (hostLifecycleState != Lifecycle.State.DESTROYED) {
            val key = savedStateKey(backStackLifecycleOwner.id)
            savedStateRegistry.consumeRestoredStateForKey(key).let { savedState ->
                backStackLifecycleOwner.restoreState(savedState ?: Bundle())
            }
            savedStateRegistry.unregisterSavedStateProvider(key)
            savedStateRegistry.registerSavedStateProvider(
                key,
                backStackLifecycleOwner.savedStateProvider
            )
        }
        backStackLifecycleOwner.navHostLifecycleState = hostLifecycleState
        backStackLifecycleOwner.maxLifecycleState = Lifecycle.State.STARTED
    }

    /**
     * When the back stack manager's container is disposed, we update all entries to the proper lifecycle
     * and remove the lifecycle observer.
     */
    fun onDispose() {
        lifeCycleEntries.values.forEach {
            it.navHostLifecycleState = Lifecycle.State.DESTROYED
        }
        hostLifecycle.removeObserver(lifecycleEventObserver)
    }

    /**
     * When an entry has been disposed from the container, we update its lifecycle and the lifecycle
     * of other entries.
     */
    fun onEntryDisposed() {
        updateLifecycles()
        cleanupEntries()
    }

    /**
     * Make sure all entries' lifecycle is up to date.
     *
     * All entries that are not in the current [visibleBackStack] will be in the destroyed state.
     *
     * We then check the entries that are in the [visibleBackStack]:
     * - If the entry is the current last entry in the [Navigator] backstack, it's resumed.
     * - If the entry is not the current last entry, then it's paused.
     */
    private fun updateLifecycles() {
        lifeCycleEntries.values.filter { it !in visibleBackStack.value.entries }
            .forEach { it.maxLifecycleState = Lifecycle.State.CREATED }

        visibleBackStack.value.entries.forEach {
            if (it.id == navigator.backStack.last().id) {
                it.maxLifecycleState = Lifecycle.State.RESUMED
            } else {
                it.maxLifecycleState = Lifecycle.State.STARTED
            }
        }
    }

    /**
     * Removes the saved state and view model store given an [entryId]
     */
    private fun removeComponents(entryId: String) {
        savedStateRegistry.unregisterSavedStateProvider(savedStateKey(entryId))
        viewModelStoreProvider.removeViewModelStore(entryId)
        saveableStateHolder.removeState(entryId)
    }

    /**
     * Destroy and remove all components of all the entries.
     */
    private fun cleanupEntries() {
        lifeCycleEntries.keys.filter { it !in backstackIds }.forEach { entryId ->
            lifeCycleEntries.remove(entryId)?.let { entry ->
                entry.maxLifecycleState = Lifecycle.State.DESTROYED
                removeComponents(entry.id)
            }
        }
    }

    /**
     * Unique key for the saved stack for the current back stack manager.
     */
    private fun savedStateKey(id: String) = "back-stack-manager-$id"
}


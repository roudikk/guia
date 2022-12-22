package com.roudikk.navigator.backstack

import android.app.Application
import android.os.Bundle
import android.os.Parcelable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.saveable.SaveableStateHolder
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
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
import com.roudikk.navigator.core.BottomSheet
import com.roudikk.navigator.core.Dialog
import com.roudikk.navigator.core.NavigationEntry
import com.roudikk.navigator.core.Navigator
import com.roudikk.navigator.core.Screen
import com.roudikk.navigator.core.navigationNode
import kotlinx.parcelize.Parcelize
import java.util.UUID

/**
 * Creates an instance a saveable instance of a [BackStackManager].
 */
@Composable
internal fun rememberBackStackManager(navigator: Navigator): BackStackManager {
    val application = LocalContext.current.applicationContext as? Application
    val viewModelStoreOwner = requireNotNull(LocalViewModelStoreOwner.current)
    val savedStateRegistry = LocalSavedStateRegistryOwner.current.savedStateRegistry
    val lifecycle = LocalLifecycleOwner.current.lifecycle

    return rememberSaveable(
        saver = Saver(
            save = {
                BackStackManagerState(
                    id = it.id,
                    entryIds = it.entryIds.toList()
                )
            },
            restore = {
                BackStackManager(
                    id = it.id,
                    restoredEntryIds = it.entryIds,
                    navigator = navigator,
                    application = application,
                    viewModelStoreOwner = viewModelStoreOwner,
                    saveableStateHolder = navigator.saveableStateHolder,
                    hostLifecycle = lifecycle,
                    savedStateRegistry = savedStateRegistry
                )
            }
        )
    ) {
        BackStackManager(
            id = UUID.randomUUID().toString(),
            restoredEntryIds = emptyList(),
            navigator = navigator,
            application = application,
            viewModelStoreOwner = viewModelStoreOwner,
            saveableStateHolder = navigator.saveableStateHolder,
            hostLifecycle = lifecycle,
            savedStateRegistry = savedStateRegistry
        )
    }
}

/**
 * Manages the BackStack state of a [Navigator].
 *
 * That includes managing the Lifecycle of each [BackStackEntry].
 * All entries in the current [Navigator]'s back stack will have a corresponding [BackStackEntry].
 */
internal class BackStackManager(
    viewModelStoreOwner: ViewModelStoreOwner,
    restoredEntryIds: List<String>,
    private val navigator: Navigator,
    internal val id: String,
    private val application: Application?,
    private val savedStateRegistry: SavedStateRegistry,
    private val saveableStateHolder: SaveableStateHolder,
    private val hostLifecycle: Lifecycle
) {
    private var hostLifecycleState: Lifecycle.State = Lifecycle.State.INITIALIZED
    private val backStackEntries = mutableMapOf<String, BackStackEntry>()
    internal val entryIds get() = backStackEntries.keys

    private val lifecycleEventObserver = LifecycleEventObserver { _, event ->
        hostLifecycleState = event.targetState
        backStackEntries.values.forEach {
            it.navHostLifecycleState = event.targetState
        }
    }

    private val viewModelStoreProvider: BackStackViewModel = ViewModelProvider(
        viewModelStoreOwner
    )["back-stack-manager-$id", BackStackViewModel::class.java]

    private val backstackIds by derivedStateOf {
        navigator.navigationEntries.map { it.id }
    }

    val backStackEntryGroup = derivedStateOf {
        val entries = navigator.navigationEntries
        val currentEntry = entries.last()

        val screenEntry = entries.lastOrNull {
            navigator.navigationNode(it) is Screen
        }?.let(::createBackStackEntry)

        val dialogEntry = currentEntry.takeIf {
            navigator.navigationNode(it) is Dialog
        }?.let(::createBackStackEntry)

        val bottomSheetEntry = entries
            .lastOrNull { navigator.navigationNode(it) is BottomSheet }
            .takeIf {
                if (currentEntry == it) return@takeIf true

                val bottomSheetIndex = entries.indexOf(it)
                val entriesAfter = entries.subList(bottomSheetIndex + 1, entries.size)
                val onlyDialogsAfter = entriesAfter.all { entry ->
                    navigator.navigationNode(entry) is Dialog
                }

                onlyDialogsAfter
            }?.let(::createBackStackEntry)

        val backStackEntryGroup = BackStackEntryGroup(
            screenEntry = screenEntry,
            dialogEntry = dialogEntry,
            bottomSheetEntry = bottomSheetEntry
        )

        backStackEntries.values
            .filter { it !in backStackEntryGroup.entries }
            .forEach { it.maxLifecycleState = minOf(it.maxLifecycleState, Lifecycle.State.STARTED) }

        val goingToDialog = navigator.navigationNode(currentEntry) is Dialog &&
            entries.getOrNull(entries.lastIndex - 1)
                ?.let(navigator::navigationNode) !is Dialog

        val goingToBottomSheet = navigator.navigationNode(currentEntry) is BottomSheet &&
            entries.getOrNull(entries.lastIndex - 1)
                ?.let(navigator::navigationNode) !is BottomSheet

        backStackEntryGroup.entries.forEach {
            if (goingToDialog || goingToBottomSheet) {
                // If the current
                if (it.id == currentEntry.id) {
                    it.maxLifecycleState = Lifecycle.State.RESUMED
                } else {
                    it.maxLifecycleState = Lifecycle.State.STARTED
                }
            } else {
                it.maxLifecycleState = Lifecycle.State.STARTED
            }
        }

        backStackEntryGroup
    }

    init {
        hostLifecycle.addObserver(lifecycleEventObserver)

        // Clear components of restored entries
        restoredEntryIds
            .filter { it !in backstackIds }
            .forEach(::removeComponents)

        // Create back stack entries for restored navigation entries
        navigator.navigationEntries
            .filter { it.id in restoredEntryIds }
            .forEach(::createBackStackEntry)

        // Make sure the back stack entries are initialized with the proper lifecycles
        updateLifecycles()
    }

    /**
     * Creates a [BackStackEntry] from the given [NavigationEntry].
     *
     * The [SaveableStateHolder] would be the state holder associated with a [Navigator].
     * The [ViewModelStore] is received from [viewModelStoreProvider] created in the back stack manager.l
     */
    private fun createBackStackEntry(navigationEntry: NavigationEntry): BackStackEntry {
        return backStackEntries.getOrPut(navigationEntry.id) {
            BackStackEntry(
                navigationEntry = navigationEntry,
                saveableStateHolder = saveableStateHolder,
                viewModelStore = viewModelStoreProvider.getViewModelStore(navigationEntry.id),
                application = application
            ).also(::initialBackStackState)
        }
    }

    /**
     * Initializes a [BackStackEntry] with the proper state.
     *
     * First, we make sure the [hostLifecycleState] is not restored before restoring the saved state.
     * Then, we update the lifecycle given the host lifecycle and [Lifecycle.State.STARTED].
     */
    private fun initialBackStackState(backStackLifecycleOwner: BackStackEntry) {
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
        backStackEntries.values.forEach {
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
     * All entries that are not in the current [backStackEntryGroup] will be in the destroyed state.
     *
     * We then check the entries that are in the [backStackEntryGroup]:
     * - If the entry is the current last entry in the [Navigator] backstack, it's resumed.
     * - If the entry is not the current last entry, then it's paused.
     */
    private fun updateLifecycles() {
        backStackEntries.values.filter { it !in backStackEntryGroup.value.entries }
            .forEach { it.maxLifecycleState = Lifecycle.State.CREATED }

        backStackEntryGroup.value.entries.forEach {
            if (it.id == navigator.navigationEntries.last().id) {
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
        backStackEntries.keys.filter { it !in backstackIds }.forEach { entryId ->
            backStackEntries.remove(entryId)?.let { entry ->
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

/**
 * Used to save and restore the state of a [BackStackManager].
 */
@Parcelize
private class BackStackManagerState(
    val id: String,
    val entryIds: List<String>
) : Parcelable

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

@Composable
internal fun rememberNavVisibleBackStackManager(navigator: Navigator): BackStackManager<NavVisibleBackStack> {
    return rememberBackStackManager(
        navigator = navigator,
        getVisibleBackStack = { backStack, createLifeCycleEntry ->
            val currentEntry = backStack.lastOrNull()
                ?: return@rememberBackStackManager NavVisibleBackStack()

            // Check if there's a valid screen that should be visible.
            // It's the last entry that is a screen.
            val screenEntry = backStack.lastOrNull {
                navigator.navigationNode(it) is Screen
            }?.let(createLifeCycleEntry)

            // Check if there's a valid dialog that should be visible.
            // It must be the last entry in the backstack.
            val dialogEntry = currentEntry.takeIf {
                navigator.navigationNode(it) is Dialog
            }?.let(createLifeCycleEntry)

            // Check if there's a valid bottom sheet that should be visible.
            // It's either the top most destination, in between a screen and a dialog
            // or just by itself.
            val bottomSheetEntry = backStack
                .lastOrNull { navigator.navigationNode(it) is BottomSheet }
                .takeIf {
                    if (currentEntry == it) return@takeIf true
                    val bottomSheetIndex = backStack.indexOf(it)
                    val entriesAfter = backStack.subList(bottomSheetIndex + 1, backStack.size)
                    entriesAfter.all { entry -> navigator.navigationNode(entry) is Dialog }
                }?.let(createLifeCycleEntry)

            val visibleBackStack = NavVisibleBackStack(
                screenEntry = screenEntry,
                dialogEntry = dialogEntry,
                bottomSheetEntry = bottomSheetEntry
            )

            val navigatingToDialog = navigator.navigationNode(currentEntry) is Dialog &&
                backStack.getOrNull(backStack.lastIndex - 1)
                    ?.let(navigator::navigationNode) !is Dialog

            val navigatingToBottomSheet = navigator.navigationNode(currentEntry) is BottomSheet &&
                backStack.getOrNull(backStack.lastIndex - 1)
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
        },
        /**
         * Make sure all entries' lifecycle is up to date.
         *
         * All entries that are not in the current visibleBackStack will be in the destroyed state.
         *
         * We then check the entries that are in the visibleBackStack:
         * - If the entry is the current last entry in the [Navigator] backstack, it's resumed.
         * - If the entry is not the current last entry, then it's paused.
         */
        updateLifeCycles = { visibleBackStack, lifeCycleEntries ->
            lifeCycleEntries.values.filter { it !in visibleBackStack.entries }
                .forEach { it.maxLifecycleState = Lifecycle.State.CREATED }

            visibleBackStack.entries.forEach {
                if (it.id == navigator.backStack.last().id) {
                    it.maxLifecycleState = Lifecycle.State.RESUMED
                } else {
                    it.maxLifecycleState = Lifecycle.State.STARTED
                }
            }
        },
    )
}

/**
 * Creates an instance a saveable instance of a [BackStackManager].
 */
@Composable
fun <VB : VisibleBackStack> rememberBackStackManager(
    navigator: Navigator,
    getVisibleBackStack: (backStack: List<BackStackEntry>, createEntry: (BackStackEntry) -> LifeCycleEntry) -> VB,
    updateLifeCycles: (visibleBackStack: VB, entries: Map<String, LifeCycleEntry>) -> Unit
): BackStackManager<VB> {
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
            saveableStateHolder = saveableStateHolder,
            getVisibleBackStack = getVisibleBackStack,
            updateLifeCycles = updateLifeCycles
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
            savedStateRegistry = savedStateRegistry,
            getVisibleBackStack = getVisibleBackStack,
            updateLifeCycles = updateLifeCycles
        ).apply { updateLifeCycles(visibleBackStack, lifeCycleEntries) }
    }
}

/**
 * Manages the BackStack state of a [Navigator].
 *
 * That includes managing the Lifecycle of each [LifeCycleEntry].
 * All entries in the current [Navigator]'s back stack will have a corresponding [LifeCycleEntry].
 */
class BackStackManager<VB : VisibleBackStack> internal constructor(
    internal val id: String,
    private val navigator: Navigator,
    private val application: Application?,
    private val savedStateRegistry: SavedStateRegistry,
    private val saveableStateHolder: SaveableStateHolder,
    private val hostLifecycle: Lifecycle,
    private val getVisibleBackStack: (
        backStack: List<BackStackEntry>,
        createEntry: (BackStackEntry) -> LifeCycleEntry
    ) -> VB,
    private val updateLifeCycles: (visibleBackStack: VB, entries: Map<String, LifeCycleEntry>) -> Unit,
    viewModelStoreOwner: ViewModelStoreOwner,
    initialEntryIds: List<String>,
) {
    val lifeCycleEntries = mutableMapOf<String, LifeCycleEntry>()

    private var hostLifecycleState: Lifecycle.State = Lifecycle.State.INITIALIZED
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

    val visibleBackStack: VB by derivedStateOf {
        getVisibleBackStack(navigator.backStack, ::createLifeCycleEntry)
            .also { updateLifeCycles(it, lifeCycleEntries) }
    }

    init {
        hostLifecycle.addObserver(lifecycleEventObserver)

        // Clear components of restored entries.
        initialEntryIds
            .filter { it !in backstackIds }
            .forEach(::removeComponents)

        // Create back stack entries for restored navigation entries.
        navigator.backStack
            .filter { it.id in initialEntryIds }
            .forEach(::createLifeCycleEntry)
    }

    /**
     * Creates a [LifeCycleEntry] from the given [BackStackEntry].
     *
     * The [SaveableStateHolder] would be the state holder associated with a [Navigator].
     * The [ViewModelStore] is received from [viewModelStoreProvider] created in the back stack manager.
     */
    fun createLifeCycleEntry(backStackEntry: BackStackEntry): LifeCycleEntry {
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
        updateLifeCycles(visibleBackStack, lifeCycleEntries)
        cleanupEntries()
    }

    /**
     * Removes the saved state and view model store given an [entryId].
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

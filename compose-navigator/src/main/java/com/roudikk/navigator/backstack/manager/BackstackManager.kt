package com.roudikk.navigator.backstack.manager

import android.os.Bundle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.saveable.SaveableStateHolder
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.LocalSavedStateRegistryOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.savedstate.SavedStateRegistry
import com.roudikk.navigator.backstack.BackstackViewModel
import com.roudikk.navigator.backstack.LifecycleEntry
import com.roudikk.navigator.backstack.VisibleBackstack
import com.roudikk.navigator.backstack.id
import com.roudikk.navigator.core.BackstackEntry
import com.roudikk.navigator.core.Navigator
import com.roudikk.navigator.savedstate.backStackManagerSaver
import java.util.UUID

/**
 * Creates an instance a saveable instance of a [BackstackManager].
 */
@Composable
fun <VB : VisibleBackstack> rememberBackstackManager(
    navigator: Navigator,
    getVisibleBackstack: (backStack: List<BackstackEntry>, createEntry: (BackstackEntry) -> LifecycleEntry) -> VB,
    updateLifecycles: (visibleBackstack: VB, entries: List<LifecycleEntry>) -> Unit
): BackstackManager<VB> {
    val viewModelStoreOwner = if (LocalInspectionMode.current) {
        ViewModelStoreOwner { ViewModelStore() }
    } else {
        requireNotNull(LocalViewModelStoreOwner.current)
    }
    val savedStateRegistry = LocalSavedStateRegistryOwner.current.savedStateRegistry
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val saveableStateHolder = rememberSaveableStateHolder()

    return rememberSaveable(
        saver = backStackManagerSaver(
            navigator = navigator,
            viewModelStoreOwner = viewModelStoreOwner,
            savedStateRegistry = savedStateRegistry,
            lifecycle = lifecycle,
            saveableStateHolder = saveableStateHolder,
            getVisibleBackstack = getVisibleBackstack,
            updateLifecycles = updateLifecycles
        )
    ) {
        BackstackManager(
            id = UUID.randomUUID().toString(),
            initialEntryIds = emptyList(),
            navigator = navigator,
            viewModelStoreOwner = viewModelStoreOwner,
            saveableStateHolder = saveableStateHolder,
            hostLifecycle = lifecycle,
            savedStateRegistry = savedStateRegistry,
            getVisibleBackstack = getVisibleBackstack,
            updateLifecycles = updateLifecycles
        ).apply { updateLifecycles(visibleBackstack, lifeCycleEntries) }
    }
}

/**
 * Manages the Backstack state of a [Navigator].
 *
 * That includes managing the Lifecycle of each [LifecycleEntry].
 * All entries in the current [Navigator]'s back stack will have a corresponding [LifecycleEntry].
 */
class BackstackManager<VB : VisibleBackstack> internal constructor(
    internal val id: String,
    private val navigator: Navigator,
    private val savedStateRegistry: SavedStateRegistry,
    private val saveableStateHolder: SaveableStateHolder,
    private val hostLifecycle: Lifecycle,
    private val getVisibleBackstack: (
        backStack: List<BackstackEntry>,
        createEntry: (BackstackEntry) -> LifecycleEntry
    ) -> VB,
    private val updateLifecycles: (
        visibleBackstack: VB,
        entries: List<LifecycleEntry>
    ) -> Unit,
    viewModelStoreOwner: ViewModelStoreOwner,
    initialEntryIds: List<String>,
) {
    private val lifeCycleEntriesMap = mutableMapOf<String, LifecycleEntry>()
    internal val lifeCycleEntries: List<LifecycleEntry>
        get() = lifeCycleEntriesMap.values.toList()

    private var hostLifecycleState: Lifecycle.State = Lifecycle.State.INITIALIZED
    internal val entryIds get() = lifeCycleEntriesMap.keys

    private val lifecycleEventObserver = LifecycleEventObserver { _, event ->
        // Update all entries with the current host life cycle state.
        hostLifecycleState = event.targetState
        lifeCycleEntries.forEach {
            it.navHostLifecycleState = event.targetState
        }
    }

    private val viewModelStoreProvider: BackstackViewModel = ViewModelProvider(
        viewModelStoreOwner
    )["back-stack-manager-$id", BackstackViewModel::class.java]

    private val backstackIds by derivedStateOf {
        navigator.backStack.map { it.id }
    }

    val visibleBackstack: VB by derivedStateOf {
        getVisibleBackstack(navigator.backStack, ::createLifecycleEntry)
            .also { updateLifecycles(it, lifeCycleEntries) }
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
            .forEach(::createLifecycleEntry)
    }

    /**
     * Creates a [LifecycleEntry] from the given [BackstackEntry].
     *
     * The [SaveableStateHolder] would be the state holder associated with a [Navigator].
     * The [ViewModelStore] is received from [viewModelStoreProvider] created in the back stack manager.
     */
    private fun createLifecycleEntry(backStackEntry: BackstackEntry): LifecycleEntry {
        return lifeCycleEntriesMap.getOrPut(backStackEntry.id) {
            LifecycleEntry(
                backStackEntry = backStackEntry,
                saveableStateHolder = saveableStateHolder,
                viewModelStore = viewModelStoreProvider.getViewModelStore(backStackEntry.id)
            ).also(::initialBackstackState)
        }
    }

    /**
     * Initializes a [LifecycleEntry] with the proper state.
     *
     * First, we make sure the [hostLifecycleState] is not restored before restoring the saved state.
     * Then, we update the lifecycle given the host lifecycle and [Lifecycle.State.STARTED].
     */
    private fun initialBackstackState(backStackLifecycleOwner: LifecycleEntry) {
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
        lifeCycleEntries.forEach {
            it.navHostLifecycleState = Lifecycle.State.DESTROYED
        }
        hostLifecycle.removeObserver(lifecycleEventObserver)
    }

    /**
     * When an entry has been disposed from the container, we update its lifecycle and the lifecycle
     * of other entries.
     */
    fun onEntryDisposed() {
        updateLifecycles(visibleBackstack, lifeCycleEntries)
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
        lifeCycleEntriesMap.keys.filter { it !in backstackIds }.forEach { entryId ->
            lifeCycleEntriesMap.remove(entryId)?.let { entry ->
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

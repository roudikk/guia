package com.roudikk.guia.lifecycle

import android.app.Application
import android.os.Bundle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
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
import com.roudikk.guia.backstack.RenderGroup
import com.roudikk.guia.core.BackstackEntry
import com.roudikk.guia.core.Navigator
import com.roudikk.guia.savedstate.lifecycleManagerSaver
import java.util.UUID

/**
 * Creates a saveable instance of a [LifecycleManager].
 */
@Composable
fun <RG : RenderGroup> rememberLifecycleManager(
    navigator: Navigator,
    getRenderGroup: GetRenderGroup<RG>,
    updateLifecycles: UpdateLifecycles<RG>
): LifecycleManager<RG> {
    val application = LocalContext.current.applicationContext as Application
    val viewModelStoreOwner = requireNotNull(LocalViewModelStoreOwner.current)
    val savedStateRegistry = LocalSavedStateRegistryOwner.current.savedStateRegistry
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val saveableStateHolder = rememberSaveableStateHolder()

    return rememberSaveable(
        saver = lifecycleManagerSaver(
            navigator = navigator,
            application = application,
            viewModelStoreOwner = viewModelStoreOwner,
            savedStateRegistry = savedStateRegistry,
            lifecycle = lifecycle,
            saveableStateHolder = saveableStateHolder,
            getRenderGroup = getRenderGroup,
            updateLifecycles = updateLifecycles
        )
    ) {
        LifecycleManager(
            id = UUID.randomUUID().toString(),
            initialEntryIds = emptyList(),
            navigator = navigator,
            application = application,
            viewModelStoreOwner = viewModelStoreOwner,
            saveableStateHolder = saveableStateHolder,
            hostLifecycle = lifecycle,
            savedStateRegistry = savedStateRegistry,
            getRenderGroup = getRenderGroup,
            updateLifecycles = updateLifecycles
        ).apply { updateLifecycles(renderGroup, lifeCycleEntries) }
    }
}

/**
 * Manages the Lifecycle state of a [Navigator]'s backstack.
 *
 * For the default behaviour, check [rememberDefaultLifecycleManager].
 *
 * To create your own use [rememberLifecycleManager] and override [getRenderGroup] and
 * [updateLifecycles] with your use case. Make sure to call [onDispose] and [onEntryDisposed]
 * according to their doc.
 */
class LifecycleManager<RG : RenderGroup> internal constructor(
    internal val id: String,
    private val navigator: Navigator,
    private val application: Application,
    private val savedStateRegistry: SavedStateRegistry,
    private val saveableStateHolder: SaveableStateHolder,
    private val hostLifecycle: Lifecycle,
    private val getRenderGroup: GetRenderGroup<RG>,
    private val updateLifecycles: UpdateLifecycles<RG>,
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

    private val viewModelStoreProvider: ViewModelStoreProvider = ViewModelProvider(
        viewModelStoreOwner
    )["back-stack-manager-$id", ViewModelStoreProvider::class.java]

    private val backstackIds by derivedStateOf {
        navigator.backstack.map { it.id }
    }

    val renderGroup: RG by derivedStateOf {
        getRenderGroup(navigator.backstack, ::createLifecycleEntry)
            .also { updateLifecycles(it, lifeCycleEntries) }
    }

    init {
        hostLifecycle.addObserver(lifecycleEventObserver)

        // Clear components of restored entries.
        initialEntryIds
            .filter { it !in backstackIds }
            .forEach(::removeComponents)

        // Create back stack entries for restored navigation entries.
        navigator.backstack
            .filter { it.id in initialEntryIds }
            .forEach(::createLifecycleEntry)
    }

    /**
     * Creates a [LifecycleEntry] from the given [BackstackEntry].
     *
     * The [SaveableStateHolder] would be the state holder associated with a [Navigator].
     * The [ViewModelStore] is received from [viewModelStoreProvider] created in the back stack manager.
     */
    private fun createLifecycleEntry(backstackEntry: BackstackEntry): LifecycleEntry {
        return lifeCycleEntriesMap.getOrPut(backstackEntry.id) {
            LifecycleEntry(
                backstackEntry = backstackEntry,
                saveableStateHolder = saveableStateHolder,
                viewModelStore = viewModelStoreProvider.getViewModelStore(backstackEntry.id),
                application = application
            ).also(::initialBackstackState)
        }
    }

    /**
     * Initializes a [LifecycleEntry] with the proper state.
     *
     * First, we make sure the [hostLifecycleState] is not destroyed before restoring the saved state.
     * Then, we update the lifecycle given the host lifecycle and [Lifecycle.State.STARTED].
     */
    private fun initialBackstackState(lifecycleEntry: LifecycleEntry) {
        if (hostLifecycleState != Lifecycle.State.DESTROYED) {
            val key = savedStateKey(lifecycleEntry.id)
            savedStateRegistry.consumeRestoredStateForKey(key).let { savedState ->
                lifecycleEntry.restoreState(savedState = savedState ?: Bundle())
            }
            savedStateRegistry.unregisterSavedStateProvider(key = key)
            savedStateRegistry.registerSavedStateProvider(
                key = key,
                provider = lifecycleEntry.savedStateProvider
            )
        }
        lifecycleEntry.navHostLifecycleState = hostLifecycleState
        lifecycleEntry.maxLifecycleState = Lifecycle.State.STARTED
    }

    /**
     * When the back stack manager's container is disposed, we update all entries to the proper lifecycle
     * and remove the lifecycle observer.
     *
     * If you're using your own Composable hosting a [LifecycleManager] make sure to call this
     * in a [DisposableEffect]'s onDispose.
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
     *
     * If you're using your own Composable hosting a Navigation Entry, make sure to call this
     * in a [DisposableEffect]'s onDispose.
     */
    fun onEntryDisposed() {
        updateLifecycles(renderGroup, lifeCycleEntries)
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

/**
 * Generates a [RenderGroup] given a [Navigator]'s current backstack.
 */
fun interface GetRenderGroup<RG : RenderGroup> {
    operator fun invoke(
        backstack: List<BackstackEntry>,
        createEntry: (BackstackEntry) -> LifecycleEntry
    ): RG
}

/**
 * Updates the lifecycle of a [LifecycleManager]'s current lifecycle entries given the
 * current [RenderGroup].
 */
fun interface UpdateLifecycles<RG : RenderGroup> {
    operator fun invoke(
        renderGroup: RG,
        lifecycleEntries: List<LifecycleEntry>
    )
}

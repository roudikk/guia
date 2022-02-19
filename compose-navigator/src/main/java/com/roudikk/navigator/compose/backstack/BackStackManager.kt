package com.roudikk.navigator.compose.backstack

import android.app.Application
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.SaveableStateHolder
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.LocalSavedStateRegistryOwner
import androidx.lifecycle.*
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.savedstate.SavedStateRegistry
import com.roudikk.navigator.Navigator
import com.roudikk.navigator.compose.NavContainer
import com.roudikk.navigator.core.*
import kotlinx.parcelize.Parcelize
import java.util.*

@Composable
internal fun rememberBackStackManager(navigator: Navigator): BackStackManager {
    val application = LocalContext.current.applicationContext as? Application
    val viewModelStoreOwner = requireNotNull(LocalViewModelStoreOwner.current)
    val savedStateRegistry = LocalSavedStateRegistryOwner.current.savedStateRegistry
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val saveableStateHolder = rememberSaveableStateHolder()

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
                    saveableStateHolder = saveableStateHolder,
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
            saveableStateHolder = saveableStateHolder,
            hostLifecycle = lifecycle,
            savedStateRegistry = savedStateRegistry
        )
    }.apply {
        navigationState = navigator.currentState
    }
}

/**
 * Manages the BackStack state of a [NavContainer].
 *
 * That includes managing the Lifecycle of each [BackStackEntry].
 * All destinations in the current navigator back stack will have a corresponding [BackStackEntry].
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

    private val viewModelStoreProvider: ViewModelStoreProvider = ViewModelProvider(
        viewModelStoreOwner
    )["back-stack-manager-$id", NavHostViewModel::class.java]

    internal var navigationState by mutableStateOf(navigator.currentState)

    private val backstackIds by derivedStateOf {
        navigationState.navigationStacks
            .map { it.destinations }
            .flatten()
            .map { it.id }
            .toHashSet()
    }

    val backStackEntryGroup = derivedStateOf {
        val destinations = navigationState.currentStack.destinations
        val currentDestination = destinations.last()

        BackStackEntryGroup(
            screenEntry = destinations.lastOrNull { it.navigationNode is Screen }
                ?.let(::createBackStackEntry),
            dialogEntry = currentDestination.takeIf { it.navigationNode is Dialog }
                ?.let(::createBackStackEntry),
            bottomSheetEntry = destinations.lastOrNull { it.navigationNode is BottomSheet }
                .takeIf {
                    currentDestination == it || (destinations.last().navigationNode is Dialog &&
                            destinations.getOrNull(destinations.lastIndex - 1) == it)
                }
                ?.let(::createBackStackEntry)
        ).also { backStackEntryGroup ->
            backStackEntries.values
                .filter { it !in backStackEntryGroup.entries }
                .forEach {
                    it.maxLifecycleState = minOf(it.maxLifecycleState, Lifecycle.State.STARTED)
                }

            backStackEntryGroup.entries
                .forEach {
                    if ((currentDestination.navigationNode is Dialog
                                && destinations.getOrNull(destinations.lastIndex - 1)?.navigationNode !is Dialog) ||
                        currentDestination.navigationNode is BottomSheet
                        && destinations.getOrNull(destinations.lastIndex - 1)?.navigationNode !is BottomSheet
                    ) {
                        if (it.id == navigator.currentState.currentStack.destinations.last().id) {
                            it.maxLifecycleState = Lifecycle.State.RESUMED
                        } else {
                            it.maxLifecycleState = Lifecycle.State.STARTED
                        }
                    } else {
                        it.maxLifecycleState = Lifecycle.State.STARTED
                    }
                }
        }
    }

    init {
        hostLifecycle.addObserver(lifecycleEventObserver)

        restoredEntryIds
            .filter { it !in backstackIds }
            .forEach(::removeComponents)

        navigationState.navigationStacks
            .map { it.destinations }
            .flatten()
            .filter { it.id in restoredEntryIds }
            .forEach(::createBackStackEntry)

        updateLifecycles()
    }

    private fun createBackStackEntry(destination: Destination): BackStackEntry {
        return backStackEntries.getOrPut(destination.id) {
            BackStackEntry(
                destination = destination,
                saveableStateHolder = saveableStateHolder,
                viewModelStore = viewModelStoreProvider.getViewModelStore(destination.id),
                application = application
            ).also(::initialBackStackState)
        }
    }

    private fun initialBackStackState(backStackLifecycleOwner: BackStackLifecycleOwner) {
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

    fun onDispose() {
        backStackEntries.values.forEach {
            it.navHostLifecycleState = Lifecycle.State.DESTROYED
        }
        hostLifecycle.removeObserver(lifecycleEventObserver)
    }

    fun onEntryDisposed() {
        updateLifecycles()
        cleanupEntries()
    }

    private fun updateLifecycles() {
        backStackEntries.values.filter { it !in backStackEntryGroup.value.entries }
            .forEach { it.maxLifecycleState = Lifecycle.State.CREATED }

        backStackEntryGroup.value.entries.forEach {
            if (it.id == navigator.currentState.currentStack.destinations.last().id) {
                it.maxLifecycleState = Lifecycle.State.RESUMED
            } else {
                it.maxLifecycleState = Lifecycle.State.STARTED
            }
        }
    }

    private fun removeComponents(entryId: String) {
        savedStateRegistry.unregisterSavedStateProvider(savedStateKey(entryId))
        viewModelStoreProvider.removeViewModelStore(entryId)
        saveableStateHolder.removeState(entryId)
    }

    private fun cleanupEntries() {
        backStackEntries.keys.filter { it !in backstackIds }.forEach { entryId ->
            backStackEntries.remove(entryId)?.let { entry ->
                entry.maxLifecycleState = Lifecycle.State.DESTROYED
                removeComponents(entry.id)
            }
        }
    }

    private fun savedStateKey(id: String) = "back-stack-manager-$id"
}

internal interface ViewModelStoreProvider {
    fun getViewModelStore(id: String): ViewModelStore
    fun removeViewModelStore(id: String)
}

internal class NavHostViewModel : ViewModel(), ViewModelStoreProvider {

    private val viewModelStores = mutableMapOf<String, ViewModelStore>()

    override fun getViewModelStore(id: String) = viewModelStores.getOrPut(id) {
        ViewModelStore()
    }

    override fun removeViewModelStore(id: String) {
        viewModelStores.remove(id)?.also { it.clear() }
    }

    override fun onCleared() {
        viewModelStores.values.forEach { it.clear() }
        viewModelStores.clear()
    }
}

@Parcelize
private data class BackStackManagerState(
    val id: String,
    val entryIds: List<String>
) : Parcelable

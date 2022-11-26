package com.roudikk.navigator.compose.backstack

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
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.savedstate.SavedStateRegistry
import com.roudikk.navigator.Navigator
import com.roudikk.navigator.core.BottomSheet
import com.roudikk.navigator.core.Destination
import com.roudikk.navigator.core.Dialog
import com.roudikk.navigator.core.Screen
import kotlinx.parcelize.Parcelize
import java.util.UUID

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
 * All destinations in the current [Navigator]'s back stack will have a corresponding [BackStackEntry].
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

    private val backstackIds by derivedStateOf {
        navigator.destinations.map { it.id }
    }

    val backStackEntryGroup = derivedStateOf {
        val destinations = navigator.destinations
        val currentDestination = destinations.last()

        val screenEntry = destinations.lastOrNull {
            navigator.navigationNode(it) is Screen
        }?.let(::createBackStackEntry)

        val dialogEntry = currentDestination.takeIf {
            navigator.navigationNode(it) is Dialog
        }?.let(::createBackStackEntry)

        val bottomSheetEntry = destinations
            .lastOrNull { navigator.navigationNode(it) is BottomSheet }
            .takeIf {
                if (currentDestination == it) return@takeIf true

                val bottomSheetIndex = destinations.indexOf(it)
                val destinationsAfter =
                    destinations.subList(bottomSheetIndex + 1, destinations.size)
                val onlyDialogsAfter =
                    destinationsAfter.all { destination -> navigator.navigationNode(destination) is Dialog }

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

        val goingToDialog = navigator.navigationNode(currentDestination) is Dialog
                && destinations.getOrNull(destinations.lastIndex - 1)
            ?.let(navigator::navigationNode) !is Dialog

        val goingToBottomSheet = navigator.navigationNode(currentDestination) is BottomSheet
                && destinations.getOrNull(destinations.lastIndex - 1)
            ?.let(navigator::navigationNode) !is BottomSheet

        backStackEntryGroup.entries.forEach {
            if (goingToDialog || goingToBottomSheet) {
                if (it.id == currentDestination.id) {
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

        restoredEntryIds
            .filter { it !in backstackIds }
            .forEach(::removeComponents)

        navigator.destinations
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
            if (it.id == navigator.destinations.last().id) {
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

    fun navigationNode(destination: Destination) = navigator.navigationNode(destination)
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

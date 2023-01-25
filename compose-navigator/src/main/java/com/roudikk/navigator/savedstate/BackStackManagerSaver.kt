package com.roudikk.navigator.savedstate

import android.os.Parcelable
import androidx.compose.runtime.saveable.SaveableStateHolder
import androidx.compose.runtime.saveable.Saver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelStoreOwner
import androidx.savedstate.SavedStateRegistry
import com.roudikk.navigator.backstack.LifecycleEntry
import com.roudikk.navigator.backstack.VisibleBackstack
import com.roudikk.navigator.backstack.manager.BackstackManager
import com.roudikk.navigator.core.BackstackEntry
import com.roudikk.navigator.core.Navigator
import kotlinx.parcelize.Parcelize

/**
 * Used to save and restore the state of a [BackstackManager].
 */
internal fun <VB : VisibleBackstack> backStackManagerSaver(
    navigator: Navigator,
    viewModelStoreOwner: ViewModelStoreOwner,
    saveableStateHolder: SaveableStateHolder,
    lifecycle: Lifecycle,
    savedStateRegistry: SavedStateRegistry,
    getVisibleBackstack: (backStack: List<BackstackEntry>, createEntry: (BackstackEntry) -> LifecycleEntry) -> VB,
    updateLifecycles: (visibleBackstack: VB, entries: List<LifecycleEntry>) -> Unit
) = Saver<BackstackManager<VB>, BackstackManagerState>(
    save = {
        BackstackManagerState(
            id = it.id,
            entryIds = it.entryIds.toList()
        )
    },
    restore = {
        BackstackManager(
            id = it.id,
            initialEntryIds = it.entryIds,
            navigator = navigator,
            viewModelStoreOwner = viewModelStoreOwner,
            saveableStateHolder = saveableStateHolder,
            hostLifecycle = lifecycle,
            savedStateRegistry = savedStateRegistry,
            getVisibleBackstack = getVisibleBackstack,
            updateLifecycles = updateLifecycles
        ).apply { updateLifecycles(visibleBackstack, lifeCycleEntries) }
    }
)

@Parcelize
class BackstackManagerState(
    val id: String,
    val entryIds: List<String>
) : Parcelable

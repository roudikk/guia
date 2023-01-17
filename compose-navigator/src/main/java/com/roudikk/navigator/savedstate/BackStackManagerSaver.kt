package com.roudikk.navigator.savedstate

import android.app.Application
import android.os.Parcelable
import androidx.compose.runtime.saveable.SaveableStateHolder
import androidx.compose.runtime.saveable.Saver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelStoreOwner
import androidx.savedstate.SavedStateRegistry
import com.roudikk.navigator.backstack.BackStackManager
import com.roudikk.navigator.backstack.LifeCycleEntry
import com.roudikk.navigator.backstack.VisibleBackStack
import com.roudikk.navigator.core.BackStackEntry
import com.roudikk.navigator.core.Navigator
import kotlinx.parcelize.Parcelize

/**
 * Used to save and restore the state of a [BackStackManager].
 */
internal fun <VB : VisibleBackStack> backStackManagerSaver(
    navigator: Navigator,
    application: Application,
    viewModelStoreOwner: ViewModelStoreOwner,
    saveableStateHolder: SaveableStateHolder,
    lifecycle: Lifecycle,
    savedStateRegistry: SavedStateRegistry,
    getVisibleBackStack: (backStack: List<BackStackEntry>, createEntry: (BackStackEntry) -> LifeCycleEntry) -> VB,
    updateLifeCycles: (visibleBackStack: VB, entries: List<LifeCycleEntry>) -> Unit
) = Saver<BackStackManager<VB>, BackStackManagerState>(
    save = {
        BackStackManagerState(
            id = it.id,
            entryIds = it.entryIds.toList()
        )
    },
    restore = {
        BackStackManager(
            id = it.id,
            initialEntryIds = it.entryIds,
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
)

@Parcelize
class BackStackManagerState(
    val id: String,
    val entryIds: List<String>
) : Parcelable

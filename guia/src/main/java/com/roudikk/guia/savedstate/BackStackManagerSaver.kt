package com.roudikk.guia.savedstate

import android.app.Application
import android.os.Parcelable
import androidx.compose.runtime.saveable.SaveableStateHolder
import androidx.compose.runtime.saveable.Saver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelStoreOwner
import androidx.savedstate.SavedStateRegistry
import com.roudikk.guia.backstack.LifecycleEntry
import com.roudikk.guia.backstack.VisibleBackstack
import com.roudikk.guia.backstack.manager.BackstackManager
import com.roudikk.guia.core.BackstackEntry
import com.roudikk.guia.core.Navigator
import kotlinx.parcelize.Parcelize

/**
 * Used to save and restore the state of a [BackstackManager].
 */
internal fun <VB : VisibleBackstack> backstackManagerSaver(
    navigator: Navigator,
    application: Application,
    viewModelStoreOwner: ViewModelStoreOwner,
    saveableStateHolder: SaveableStateHolder,
    lifecycle: Lifecycle,
    savedStateRegistry: SavedStateRegistry,
    getVisibleBackstack: (backstack: List<BackstackEntry>, createEntry: (BackstackEntry) -> LifecycleEntry) -> VB,
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
            application = application,
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

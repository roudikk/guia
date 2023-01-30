package com.roudikk.guia.savedstate

import android.app.Application
import android.os.Parcelable
import androidx.compose.runtime.saveable.SaveableStateHolder
import androidx.compose.runtime.saveable.Saver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelStoreOwner
import androidx.savedstate.SavedStateRegistry
import com.roudikk.guia.backstack.RenderGroup
import com.roudikk.guia.core.Navigator
import com.roudikk.guia.lifecycle.GetRenderGroup
import com.roudikk.guia.lifecycle.LifecycleManager
import com.roudikk.guia.lifecycle.UpdateLifecycles
import kotlinx.parcelize.Parcelize

/**
 * Used to save and restore the state of a [LifecycleManager].
 */
internal fun <RG : RenderGroup> lifecycleManagerSaver(
    navigator: Navigator,
    application: Application,
    viewModelStoreOwner: ViewModelStoreOwner,
    saveableStateHolder: SaveableStateHolder,
    lifecycle: Lifecycle,
    savedStateRegistry: SavedStateRegistry,
    getRenderGroup: GetRenderGroup<RG>,
    updateLifecycles: UpdateLifecycles<RG>
) = Saver<LifecycleManager<RG>, LifecycleManagerState>(
    save = {
        LifecycleManagerState(
            id = it.id,
            entryIds = it.entryIds.toList()
        )
    },
    restore = {
        LifecycleManager(
            id = it.id,
            initialEntryIds = it.entryIds,
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
)

@Parcelize
class LifecycleManagerState(
    val id: String,
    val entryIds: List<String>
) : Parcelable

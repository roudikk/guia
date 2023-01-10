package com.roudikk.navigator.savedstate

import android.app.Application
import android.os.Parcelable
import androidx.compose.runtime.saveable.SaveableStateHolder
import androidx.compose.runtime.saveable.Saver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelStoreOwner
import androidx.savedstate.SavedStateRegistry
import com.roudikk.navigator.backstack.BackStackManager
import com.roudikk.navigator.core.Navigator
import kotlinx.parcelize.Parcelize

internal fun backStackManagerSaver(
    navigator: Navigator,
    application: Application,
    viewModelStoreOwner: ViewModelStoreOwner,
    saveableStateHolder: SaveableStateHolder,
    lifecycle: Lifecycle,
    savedStateRegistry: SavedStateRegistry
) = Saver<BackStackManager, BackStackManagerState>(
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
            savedStateRegistry = savedStateRegistry
        )
    }
)

/**
 * Used to save and restore the state of a [BackStackManager].
 */
@Parcelize
internal class BackStackManagerState(
    val id: String,
    val entryIds: List<String>
) : Parcelable

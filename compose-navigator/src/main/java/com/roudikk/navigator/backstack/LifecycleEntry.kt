package com.roudikk.navigator.backstack

import android.app.Application
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.saveable.SaveableStateHolder
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.LocalSavedStateRegistryOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.savedstate.SavedStateRegistry
import androidx.savedstate.SavedStateRegistryController
import com.roudikk.navigator.core.BackStackEntry
import com.roudikk.navigator.core.NavigationNode

/**
 * [LifecycleEntry] for a [BackStackEntry].
 *
 * Each entry will have a single [LifecycleEntry] representing it.
 *
 * Unlike [BackStackEntry] which only contains the [NavigationNode], [LifecycleEntry] provides
 * access to [Lifecycle], [ViewModelStore] and [SavedStateRegistry] critical to handling screen
 * state restoration and [ViewModel] creation and restoration.
 */
internal class LifecycleEntry(
    val backStackEntry: BackStackEntry,
    application: Application?,
    viewModelStore: ViewModelStore,
    saveableStateHolder: SaveableStateHolder,
) : BackStackLifecycleOwner(viewModelStore, application, saveableStateHolder) {

    override val id get() = backStackEntry.id
    override val lifecycleRegistry = LifecycleRegistry(this)
    override val savedStateRegistryController = SavedStateRegistryController.create(this)

    override fun equals(other: Any?): Boolean {
        return this.id == (other as? LifecycleEntry)?.id
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
}

/**
 * Provides [ViewModelStore], [LifecycleOwner] and [SavedStateRegistry] to [content].
 */
@Composable
internal fun LifecycleEntry.LocalProvider(
    content: @Composable () -> Unit
) = CompositionLocalProvider(
    LocalViewModelStoreOwner provides this,
    LocalLifecycleOwner provides this,
    LocalSavedStateRegistryOwner provides this
) {
    SaveableStateProvider(content)
}

package com.roudikk.navigator.compose.backstack

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
import com.roudikk.navigator.core.NavigationEntry
import com.roudikk.navigator.core.NavigationNode

/**
 * [BackStackEntry] for a [NavigationEntry].
 *
 * Each destination will have a single [BackStackEntry] representing it.
 *
 * Unlike [NavigationEntry] which only contains the [NavigationNode], [BackStackEntry] provides
 * access to [Lifecycle], [ViewModelStore] and [SavedStateRegistry] critical to handling screen
 * state restoration and [ViewModel] creation and restoration.
 */
internal class BackStackEntry(
    val navigationEntry: NavigationEntry,
    application: Application?,
    viewModelStore: ViewModelStore,
    saveableStateHolder: SaveableStateHolder,
) : BackStackLifecycleOwner(viewModelStore, application, saveableStateHolder) {

    override val id get() = navigationEntry.id
    override val lifecycleRegistry = LifecycleRegistry(this)
    override val savedStateRegistryController = SavedStateRegistryController.create(this)

    override fun equals(other: Any?): Boolean {
        return this.id == (other as? BackStackEntry)?.id
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
}

/**
 * Provides [ViewModelStore], [LifecycleOwner] and [SavedStateRegistry] to [content].
 */
@Composable
internal fun BackStackEntry.LocalProvider(
    content: @Composable () -> Unit
) = CompositionLocalProvider(
    LocalViewModelStoreOwner provides this,
    LocalLifecycleOwner provides this,
    LocalSavedStateRegistryOwner provides this
) {
    SaveableStateProvider(content)
}

package com.roudikk.navigator.backstack

import android.app.Application
import android.os.Bundle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.saveable.SaveableStateHolder
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.LocalSavedStateRegistryOwner
import androidx.lifecycle.HasDefaultViewModelProviderFactory
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.SavedStateViewModelFactory
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.savedstate.SavedStateRegistry
import androidx.savedstate.SavedStateRegistryController
import androidx.savedstate.SavedStateRegistryOwner
import com.roudikk.navigator.core.BackStackEntry
import com.roudikk.navigator.core.NavigationNode

/**
 * [LifeCycleEntry] for a [BackStackEntry].
 *
 * Each entry will have a single [LifeCycleEntry] representing it.
 *
 * Unlike [BackStackEntry] which only contains the [NavigationNode], [LifeCycleEntry] provides
 * access to [Lifecycle], [ViewModelStore] and [SavedStateRegistry] critical to handling screen
 * state restoration and [ViewModel] creation and restoration.
 */
class LifeCycleEntry(
    application: Application?,
    val backStackEntry: BackStackEntry,
    val saveableStateHolder: SaveableStateHolder,
    private val viewModelStore: ViewModelStore,
) : ViewModelStoreOwner,
    LifecycleOwner,
    SavedStateRegistryOwner,
    HasDefaultViewModelProviderFactory {

    private val lifecycleRegistry = LifecycleRegistry(this)
    private val savedStateRegistryController = SavedStateRegistryController.create(this)

    var navHostLifecycleState = Lifecycle.State.INITIALIZED
        set(value) {
            field = value
            updateLifecycleRegistry()
        }

    var maxLifecycleState = Lifecycle.State.INITIALIZED
        set(value) {
            field = value
            updateLifecycleRegistry()
        }

    val savedStateProvider = SavedStateRegistry.SavedStateProvider {
        Bundle().also(savedStateRegistryController::performSave)
    }

    private val defaultFactory by lazy {
        SavedStateViewModelFactory(application, this)
    }

    private fun updateLifecycleRegistry() {
        lifecycleRegistry.currentState = minOf(maxLifecycleState, navHostLifecycleState)
    }

    fun restoreState(savedState: Bundle) {
        savedStateRegistryController.performRestore(savedState)
    }

    override val savedStateRegistry: SavedStateRegistry
        get() = savedStateRegistryController.savedStateRegistry

    override fun getViewModelStore() = viewModelStore

    override fun getLifecycle(): Lifecycle = lifecycleRegistry

    override fun getDefaultViewModelProviderFactory() = defaultFactory

    override fun equals(other: Any?) = (this.id == (other as? LifeCycleEntry)?.id)

    override fun hashCode() = id.hashCode()
}

/**
 * Provides [ViewModelStore], [LifecycleOwner] and [SavedStateRegistry] to [content].
 */
@Composable
internal fun LifeCycleEntry.LocalProvider(
    content: @Composable () -> Unit
) = CompositionLocalProvider(
    LocalViewModelStoreOwner provides this,
    LocalLifecycleOwner provides this,
    LocalSavedStateRegistryOwner provides this
) {
    SaveableStateProvider(content)
}

val LifeCycleEntry.id: String
    get() = backStackEntry.id

@Composable
internal fun LifeCycleEntry.SaveableStateProvider(
    content: @Composable () -> Unit
) = saveableStateHolder.SaveableStateProvider(
    key = id,
    content = content
)

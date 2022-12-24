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
import kotlin.properties.Delegates

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
    private val viewModelStore: ViewModelStore,
    internal val saveableStateHolder: SaveableStateHolder,
) : ViewModelStoreOwner,
    LifecycleOwner,
    SavedStateRegistryOwner,
    HasDefaultViewModelProviderFactory {

    private val lifecycleRegistry = LifecycleRegistry(this)
    private val savedStateRegistryController = SavedStateRegistryController.create(this)

    override fun equals(other: Any?): Boolean {
        return this.id == (other as? LifecycleEntry)?.id
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }

    internal var navHostLifecycleState by Delegates.observable(Lifecycle.State.INITIALIZED) { _, _, _ ->
        updateLifecycleRegistry()
    }

    internal var maxLifecycleState by Delegates.observable(Lifecycle.State.INITIALIZED) { _, _, _ ->
        updateLifecycleRegistry()
    }

    internal val savedStateProvider = SavedStateRegistry.SavedStateProvider {
        Bundle().also { bundle ->
            savedStateRegistryController.performSave(bundle)
        }
    }

    private val defaultFactory by lazy {
        SavedStateViewModelFactory(application, this)
    }

    override val savedStateRegistry: SavedStateRegistry
        get() = savedStateRegistryController.savedStateRegistry

    override fun getViewModelStore() = viewModelStore

    override fun getLifecycle(): Lifecycle = lifecycleRegistry

    override fun getDefaultViewModelProviderFactory() = defaultFactory

    private fun updateLifecycleRegistry() {
        lifecycleRegistry.currentState = minOf(maxLifecycleState, navHostLifecycleState)
    }

    internal fun restoreState(savedState: Bundle) {
        savedStateRegistryController.performRestore(savedState)
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

internal val LifecycleEntry.id: String
    get() = backStackEntry.id

@Composable
internal fun LifecycleEntry.SaveableStateProvider(content: @Composable () -> Unit) =
    saveableStateHolder.SaveableStateProvider(
        key = id,
        content = content
    )

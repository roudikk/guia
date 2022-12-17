package com.roudikk.navigator.backstack

import android.app.Application
import android.os.Bundle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.saveable.SaveableStateHolder
import androidx.lifecycle.HasDefaultViewModelProviderFactory
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.SavedStateViewModelFactory
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.savedstate.SavedStateRegistry
import androidx.savedstate.SavedStateRegistryController
import androidx.savedstate.SavedStateRegistryOwner
import kotlin.properties.Delegates

/**
 * Defines a lifecycle owner.
 *
 * A [BackStackLifecycleOwner] allows:
 * - Saving state bundle through [SavedStateRegistry]
 * - Proving [ViewModel]s through [ViewModelStore]
 * - Lifecycle handling since its a [LifecycleOwner]
 */
internal abstract class BackStackLifecycleOwner(
    private val viewModelStore: ViewModelStore,
    private val application: Application?,
    private val saveableStateHolder: SaveableStateHolder,
) : ViewModelStoreOwner,
    LifecycleOwner,
    SavedStateRegistryOwner,
    HasDefaultViewModelProviderFactory {

    abstract val id: String

    abstract val lifecycleRegistry: LifecycleRegistry
    abstract val savedStateRegistryController: SavedStateRegistryController

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
        SavedStateViewModelFactory(application, this, null)
    }

    override val savedStateRegistry: SavedStateRegistry
        get() = savedStateRegistryController.savedStateRegistry

    override fun getViewModelStore() = viewModelStore

    override fun getLifecycle(): Lifecycle = lifecycleRegistry

    open fun updateLifecycleRegistry() {
        lifecycleRegistry.currentState = minOf(maxLifecycleState, navHostLifecycleState)
    }

    internal fun restoreState(savedState: Bundle) {
        savedStateRegistryController.performRestore(savedState)
    }

    override fun getDefaultViewModelProviderFactory() = defaultFactory

    @Composable
    internal fun SaveableStateProvider(content: @Composable () -> Unit) =
        saveableStateHolder.SaveableStateProvider(
            key = id,
            content = content
        )
}

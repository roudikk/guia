package com.roudikk.guia.lifecycle

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelStore

/**
 * Dictionary for storing and accessing the [ViewModel] entries inside a [LifecycleManager].
 */
internal class ViewModelStoreProvider : ViewModel() {

    private val viewModelStores = mutableMapOf<String, ViewModelStore>()

    fun getViewModelStore(id: String) = viewModelStores.getOrPut(id) {
        ViewModelStore()
    }

    fun removeViewModelStore(id: String) {
        viewModelStores.remove(id)?.also { it.clear() }
    }

    override fun onCleared() {
        viewModelStores.values.forEach { it.clear() }
        viewModelStores.clear()
    }
}

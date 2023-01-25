package com.roudikk.navigator.backstack

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelStore
import com.roudikk.navigator.backstack.manager.BackstackManager

/**
 * Dictionary for storing and accessing the [ViewModel] entries inside a [BackstackManager].
 */
internal class BackstackViewModel : ViewModel() {

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

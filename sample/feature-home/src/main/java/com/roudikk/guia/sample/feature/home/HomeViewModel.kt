package com.roudikk.guia.sample.feature.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import java.util.UUID

sealed class HomeEvent {
    data class OpenDetails(val item: String) : HomeEvent()
    data class ShowToast(val item: String) : HomeEvent()
    data class RefreshResult(val item: String) : HomeEvent()
    object OpenSettings : HomeEvent()
    object ClearResult : HomeEvent()
}

class HomeViewModel(
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {

    var listItems = mutableStateListOf<String>()
        private set

    var event by mutableStateOf<HomeEvent?>(null)
        private set

    init {
        listItems.addAll(savedStateHandle["items"] ?: emptyList())
    }

    private fun newItem() = UUID.randomUUID().toString().split("-")[0]

    fun onAddItemSelected() {
        listItems.add(newItem())
        savedStateHandle["items"] = ArrayList(listItems)
    }

    fun onRemoveItemSelected(item: String) {
        listItems.remove(item)
        savedStateHandle["items"] = ArrayList(listItems)
    }

    fun onClearAllSelected() {
        listItems.clear()
        savedStateHandle["items"] = arrayListOf<String>()
    }

    fun onItemSelected(item: String) {
        event = HomeEvent.OpenDetails(item)
    }

    fun onSettingsSelected() {
        event = HomeEvent.OpenSettings
    }

    fun onClearResultSelected() {
        event = HomeEvent.ClearResult
    }

    fun onRefreshResultSelected() {
        event = HomeEvent.RefreshResult(newItem())
    }

    fun onEventHandled() {
        event = null
    }
}

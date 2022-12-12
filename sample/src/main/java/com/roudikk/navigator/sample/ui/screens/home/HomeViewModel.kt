package com.roudikk.navigator.sample.ui.screens.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.roudikk.navigator.sample.ui.screens.details.DetailsResult
import java.util.UUID

sealed class HomeCommand {
    data class OpenDetails(val item: String) : HomeCommand()
    object OpenSettings : HomeCommand()
    data class ShowToast(val item: String) : HomeCommand()
}

class HomeViewModel(
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {

    var listItems = mutableStateListOf<String>()
        private set

    var command by mutableStateOf<HomeCommand?>(null)
        private set

    init {
        listItems.addAll(savedStateHandle["items"] ?: emptyList())
    }

    fun onAddItemSelected() {
        listItems.add(UUID.randomUUID().toString().split("-")[0])
        savedStateHandle["items"] = listItems.toList()
    }

    fun onRemoveItemSelected(item: String) {
        listItems.remove(item)
        savedStateHandle["items"] = listItems.toList()
    }

    fun onItemSelected(item: String) {
        command = HomeCommand.OpenDetails(item)
    }

    fun onClearAllSelected() {
        listItems.clear()
        savedStateHandle["items"] = emptyList<String>()
    }

    fun onSettingsSelected() {
        command = HomeCommand.OpenSettings
    }

    fun onDetailsResult(result: DetailsResult) {
        command = HomeCommand.ShowToast(result.value)
    }

    fun onCommandHandled() {
        command = null
    }
}

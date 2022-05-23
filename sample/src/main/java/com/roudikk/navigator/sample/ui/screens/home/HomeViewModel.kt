package com.roudikk.navigator.sample.ui.screens.home

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.*

sealed class HomeCommand {
    data class OpenDetails(val item: String) : HomeCommand()
    object OpenSettings : HomeCommand()
}

class HomeViewModel(
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val mutableStateFlow = MutableStateFlow<List<String>>(
        savedStateHandle["items"] ?: emptyList()
    )
    val stateFlow: StateFlow<List<String>> = mutableStateFlow

    private val mutableCommandsFlow = MutableSharedFlow<HomeCommand>(extraBufferCapacity = 1)
    val commandsFlow: Flow<HomeCommand> = mutableCommandsFlow

    fun onAddItemSelected() {
        val newList = mutableListOf<String>().apply {
            addAll(mutableStateFlow.value)
        }
        newList.add(UUID.randomUUID().toString().split("-")[0])
        savedStateHandle["items"] = newList
        mutableStateFlow.value = newList
    }

    fun onRemoveItemSelected(item: String) {
        val newList = mutableListOf<String>().apply {
            addAll(mutableStateFlow.value)
            remove(item)
        }
        savedStateHandle["items"] = newList
        mutableStateFlow.value = newList
    }

    fun onItemSelected(item: String) {
        mutableCommandsFlow.tryEmit(HomeCommand.OpenDetails(item))
    }

    fun onClearAllSelected() {
        val newList = emptyList<String>()
        savedStateHandle["items"] = newList
        mutableStateFlow.value = newList
    }

    fun onSettingsSelected() {
        mutableCommandsFlow.tryEmit(HomeCommand.OpenSettings)
    }
}

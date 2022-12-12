package com.roudikk.navigator.sample.ui.screens.details

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import java.util.UUID

sealed class DetailsEvent {
    object GoBack : DetailsEvent()
    data class OpenRandomItem(val item: String) : DetailsEvent()
    data class SendResult(val result: String) : DetailsEvent()
    data class OpenBottomSheet(val item: String) : DetailsEvent()
    data class OpenNewSingleInstance(val item: String) : DetailsEvent()
    data class OpenDynamicItem(val item: String) : DetailsEvent()
    data class OpenExistingSingleInstance(val item: String) : DetailsEvent()
    data class OpenSingleTop(val item: String) : DetailsEvent()
    data class OpenSingleTopBottomSheet(val item: String) : DetailsEvent()
    data class OpenReplaced(val item: String) : DetailsEvent()
    data class OpenDialog(val item: String) : DetailsEvent()
}

class DetailsViewModel(
    val item: String
) : ViewModel() {

    var event by mutableStateOf<DetailsEvent?>(null)
        private set 
    
    private fun newItem() = UUID.randomUUID().toString().split("-")[0]

    fun onBackSelected() {
        event = DetailsEvent.GoBack
    }

    fun onRandomItemSelected() {
        event = DetailsEvent.OpenRandomItem(newItem())
    }

    fun onSendResultSelected() {
        event = DetailsEvent.SendResult(item)
    }

    fun onBottomSheetSelected() {
        event = DetailsEvent.OpenBottomSheet(newItem())
    }

    fun onNewSingleInstanceSelected() {
        event = DetailsEvent.OpenNewSingleInstance(newItem())
    }

    fun onExistingSingleInstanceSelected() {
        event = DetailsEvent.OpenExistingSingleInstance(newItem())
    }

    fun onSingleTopSelected() {
        event = DetailsEvent.OpenSingleTop(newItem())
    }

    fun onSingleTopBottomSheetSelected() {
        event = DetailsEvent.OpenSingleTopBottomSheet(newItem())
    }

    fun onReplaceSelected() {
        event = DetailsEvent.OpenReplaced(newItem())
    }

    fun onOpenDialogSelected() {
        event = DetailsEvent.OpenDialog(newItem())
    }

    fun onDynamicSelected() {
        event = DetailsEvent.OpenDynamicItem(newItem())
    }

    fun onEventHandled() {
        event = null
    }
}

package com.roudikk.navigator.sample.ui.screens.details

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import java.util.UUID

sealed class DetailsCommand {
    object GoBack : DetailsCommand()
    data class OpenRandomItem(val item: String) : DetailsCommand()
    data class SendResult(val result: String) : DetailsCommand()
    data class OpenBottomSheet(val item: String) : DetailsCommand()
    data class OpenNewSingleInstance(val item: String) : DetailsCommand()
    data class OpenDynamicItem(val item: String) : DetailsCommand()
    data class OpenExistingSingleInstance(val item: String) : DetailsCommand()
    data class OpenSingleTop(val item: String) : DetailsCommand()
    data class OpenSingleTopBottomSheet(val item: String) : DetailsCommand()
    data class OpenReplaced(val item: String) : DetailsCommand()
    data class OpenDialog(val item: String) : DetailsCommand()
}

class DetailsViewModel(
    val item: String
) : ViewModel() {

    var command by mutableStateOf<DetailsCommand?>(null)
        private set 
    
    private fun newItem() = UUID.randomUUID().toString().split("-")[0]

    fun onBackSelected() {
        command = DetailsCommand.GoBack
    }

    fun onRandomItemSelected() {
        command = DetailsCommand.OpenRandomItem(newItem())
    }

    fun onSendResultSelected() {
        command = DetailsCommand.SendResult(item)
    }

    fun onBottomSheetSelected() {
        command = DetailsCommand.OpenBottomSheet(newItem())
    }

    fun onNewSingleInstanceSelected() {
        command = DetailsCommand.OpenNewSingleInstance(newItem())
    }

    fun onExistingSingleInstanceSelected() {
        command = DetailsCommand.OpenExistingSingleInstance(newItem())
    }

    fun onSingleTopSelected() {
        command = DetailsCommand.OpenSingleTop(newItem())
    }

    fun onSingleTopBottomSheetSelected() {
        command = DetailsCommand.OpenSingleTopBottomSheet(newItem())
    }

    fun onReplaceSelected() {
        command = DetailsCommand.OpenReplaced(newItem())
    }

    fun onOpenDialogSelected() {
        command = DetailsCommand.OpenDialog(newItem())
    }

    fun onDynamicSelected() {
        command = DetailsCommand.OpenDynamicItem(newItem())
    }

    fun onCommandHandled() {
        command = null
    }
}

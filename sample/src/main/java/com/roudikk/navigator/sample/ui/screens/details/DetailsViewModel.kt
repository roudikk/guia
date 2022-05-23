package com.roudikk.navigator.sample.ui.screens.details

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import java.util.*

sealed class DetailsCommand {
    object GoBack : DetailsCommand()
    data class OpenRandomItem(val item: String) : DetailsCommand()
    data class SendResult(val result: String) : DetailsCommand()
    data class OpenBottomSheet(val item: String) : DetailsCommand()
    data class OpenNewSingleInstance(val item: String) : DetailsCommand()
    data class OpenExistingSingleInstance(val item: String) : DetailsCommand()
    data class OpenSingleTop(val item: String) : DetailsCommand()
    data class OpenSingleTopBottomSheet(val item: String) : DetailsCommand()
    data class OpenReplaced(val item: String) : DetailsCommand()
    data class OpenDialog(val item: String) : DetailsCommand()
}

class DetailsViewModel(val item: String) : ViewModel() {

    private val mutableCommandsFlow = MutableSharedFlow<DetailsCommand>(extraBufferCapacity = 1)
    val commandsFlow: Flow<DetailsCommand> = mutableCommandsFlow

    private fun newItem() = UUID.randomUUID().toString().split("-")[0]

    fun onBackSelected() {
        mutableCommandsFlow.tryEmit(DetailsCommand.GoBack)
    }

    fun onRandomItemSelected() {
        mutableCommandsFlow.tryEmit(DetailsCommand.OpenRandomItem(newItem()))
    }

    fun onSendResultSelected() {
        mutableCommandsFlow.tryEmit(DetailsCommand.SendResult(item))
    }

    fun onBottomSheetSelected() {
        mutableCommandsFlow.tryEmit(DetailsCommand.OpenBottomSheet(newItem()))
    }

    fun onNewSingleInstanceSelected() {
        mutableCommandsFlow.tryEmit(DetailsCommand.OpenNewSingleInstance(newItem()))
    }

    fun onExistingSingleInstanceSelected() {
        mutableCommandsFlow.tryEmit(DetailsCommand.OpenExistingSingleInstance(newItem()))
    }

    fun onSingleTopSelected() {
        mutableCommandsFlow.tryEmit(DetailsCommand.OpenSingleTop(newItem()))
    }

    fun onSingleTopBottomSheetSelected() {
        mutableCommandsFlow.tryEmit(DetailsCommand.OpenSingleTopBottomSheet(newItem()))
    }

    fun onReplaceSelected() {
        mutableCommandsFlow.tryEmit(DetailsCommand.OpenReplaced(newItem()))
    }

    fun onOpenDialogSelected() {
        mutableCommandsFlow.tryEmit(DetailsCommand.OpenDialog(newItem()))
    }
}

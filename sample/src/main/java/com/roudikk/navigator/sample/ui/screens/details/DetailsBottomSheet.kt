package com.roudikk.navigator.sample.ui.screens.details

import android.util.Log
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import com.roudikk.navigator.compose.LifecycleEffect
import com.roudikk.navigator.compose.requireNavigator
import com.roudikk.navigator.core.BottomSheet
import com.roudikk.navigator.core.BottomSheetOptions
import kotlinx.parcelize.Parcelize

@Parcelize
class DetailsBottomSheet(
    private val item: String
) : BottomSheet {

    override val bottomSheetOptions: BottomSheetOptions
        get() = BottomSheetOptions(
            confirmStateChange = {
                it != ModalBottomSheetValue.HalfExpanded
            }
        )

    @Composable
    override fun Content() {
        val viewModel = viewModel { DetailsViewModel(item) }

        DetailsList(
            item = viewModel.item,
            onRandomItemSelected = viewModel::onRandomItemSelected,
            onSendResultSelected = viewModel::onSendResultSelected,
            onBottomSheetSelected = viewModel::onBottomSheetSelected,
            onNewSingleInstanceSelected = viewModel::onNewSingleInstanceSelected,
            onExistingSingleInstanceSelected = viewModel::onExistingSingleInstanceSelected,
            onSingleTopSelected = viewModel::onSingleTopSelected,
            onSingleTopBottomSheetSelected = viewModel::onSingleTopBottomSheetSelected,
            onReplaceSelected = viewModel::onReplaceSelected,
            onOpenDialogSelected = viewModel::onOpenDialogSelected
        )

        DetailsCommandHandler(
            navigator = requireNavigator(),
            viewModel = viewModel
        )
    }
}

package com.roudikk.navigator.sample.ui.screens.details

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.roudikk.navigator.compose.requireNavigator
import com.roudikk.navigator.core.Dialog
import kotlinx.parcelize.Parcelize

@Parcelize
class DetailsDialog(private val item: String) : Dialog {

    @Composable
    override fun Content() = Surface(
        shape = RoundedCornerShape(16.dp)
    ) {
        Box(modifier = Modifier.padding(16.dp)) {

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
}

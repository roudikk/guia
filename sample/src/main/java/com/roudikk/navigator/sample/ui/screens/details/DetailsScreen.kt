package com.roudikk.navigator.sample.ui.screens.details

import android.content.res.Configuration
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.roudikk.navigator.compose.requireNavigator
import com.roudikk.navigator.core.Screen
import com.roudikk.navigator.sample.ui.composables.AppTopAppBar
import com.roudikk.navigator.sample.ui.theme.AppTheme
import kotlinx.parcelize.Parcelize

@Parcelize
class DetailsScreen(
    private val item: String
) : Screen {

    @Composable
    override fun Content() {
        val viewModel = viewModel { DetailsViewModel(item) }

        DetailsContent(
            item = viewModel.item,
            onBackSelected = viewModel::onBackSelected,
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

@Composable
private fun DetailsContent(
    item: String,
    onBackSelected: () -> Unit,
    onRandomItemSelected: () -> Unit,
    onSendResultSelected: () -> Unit,
    onBottomSheetSelected: () -> Unit,
    onNewSingleInstanceSelected: () -> Unit,
    onExistingSingleInstanceSelected: () -> Unit,
    onSingleTopSelected: () -> Unit,
    onSingleTopBottomSheetSelected: () -> Unit,
    onReplaceSelected: () -> Unit,
    onOpenDialogSelected: () -> Unit
) {
    Scaffold(
        topBar = {
            AppTopAppBar(
                title = "Details",
                navigationIcon = {
                    IconButton(
                        onClick = onBackSelected
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        },
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            DetailsList(
                item = item,
                onRandomItemSelected = onRandomItemSelected,
                onSendResultSelected = onSendResultSelected,
                onBottomSheetSelected = onBottomSheetSelected,
                onNewSingleInstanceSelected = onNewSingleInstanceSelected,
                onExistingSingleInstanceSelected = onExistingSingleInstanceSelected,
                onSingleTopSelected = onSingleTopSelected,
                onSingleTopBottomSheetSelected = onSingleTopBottomSheetSelected,
                onReplaceSelected = onReplaceSelected,
                onOpenDialogSelected = onOpenDialogSelected
            )
        }
    }
}

@Preview(
    device = Devices.PIXEL_3
)
@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    device = Devices.PIXEL_3
)
@Composable
private fun DetailsContentPreview() = AppTheme {
    DetailsContent(
        item = "Test Item!",
        onBackSelected = { },
        onRandomItemSelected = { },
        onSendResultSelected = { },
        onBottomSheetSelected = { },
        onNewSingleInstanceSelected = { },
        onExistingSingleInstanceSelected = { },
        onSingleTopSelected = { },
        onSingleTopBottomSheetSelected = { },
        onReplaceSelected = { },
        onOpenDialogSelected = { }
    )
}

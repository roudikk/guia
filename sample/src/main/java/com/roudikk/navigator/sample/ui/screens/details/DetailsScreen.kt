package com.roudikk.navigator.sample.ui.screens.details

import android.content.res.Configuration
import android.os.Parcelable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.roudikk.navigator.NavigationKey
import com.roudikk.navigator.NavigatorRulesScope
import com.roudikk.navigator.compose.requireNavigator
import com.roudikk.navigator.core.DialogOptions
import com.roudikk.navigator.sample.ui.composables.AppTopAppBar
import com.roudikk.navigator.sample.ui.composables.BottomSheetSurface
import com.roudikk.navigator.sample.ui.theme.AppTheme
import kotlinx.parcelize.Parcelize

@Parcelize
data class DetailsKey(val item: String) : Parcelable, NavigationKey

@Parcelize
data class DetailsDialogKey(val item: String) : Parcelable, NavigationKey

@Parcelize
data class DetailsBottomSheetKey(val item: String) : Parcelable, NavigationKey

fun NavigatorRulesScope.detailsNavigation() {
    screen<DetailsKey> { key -> DetailsScreen(item = key.item, isScreen = true) }

    dialog<DetailsDialogKey>(
        dialogOptions = DialogOptions(modifier = Modifier.widthIn(max = 320.dp))
    ) { key -> DetailsScreen(item = key.item, isScreen = false) }

    bottomSheet<DetailsBottomSheetKey> { key -> DetailsScreen(item = key.item, isScreen = false) }
}

@Composable
private fun DetailsScreen(
    item: String,
    isScreen: Boolean
) {
    val viewModel: DetailsViewModel = viewModel { DetailsViewModel(item, isScreen) }

    DetailsContent(
        item = viewModel.item,
        isScreen = viewModel.isScreen,
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

@Composable
private fun DetailsContent(
    item: String,
    isScreen: Boolean,
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
    val content = @Composable {
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

    if (isScreen) {
        Scaffold(
            topBar = {
                AppTopAppBar(
                    title = "Details",
                    navigationIcon = {
                        IconButton(onClick = onBackSelected) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Back"
                            )
                        }
                    }
                )
            },
        ) { padding ->
            Box(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                content()
            }
        }
    } else {
        BottomSheetSurface {
            content()
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
        isScreen = true,
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

@Preview(
    device = Devices.PIXEL_3
)
@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    device = Devices.PIXEL_3
)
@Composable
private fun DetailsContentPreviewOverlay() = AppTheme {
    DetailsContent(
        item = "Test Item!",
        isScreen = false,
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

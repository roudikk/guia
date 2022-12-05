package com.roudikk.navigator.sample.ui.screens.details

import android.content.res.Configuration
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
import androidx.compose.runtime.movableContentOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.roudikk.navigator.compose.animation.NavigationTransition
import com.roudikk.navigator.compose.requireNavigator
import com.roudikk.navigator.core.Dialog
import com.roudikk.navigator.core.DialogOptions
import com.roudikk.navigator.core.NavigationKey
import com.roudikk.navigator.core.NavigatorRulesBuilder
import com.roudikk.navigator.core.SimpleNavigationKey
import com.roudikk.navigator.core.dialogNode
import com.roudikk.navigator.sample.navigation.CrossFadeTransition
import com.roudikk.navigator.sample.ui.composables.AppTopAppBar
import com.roudikk.navigator.sample.ui.composables.BottomSheetSurface
import com.roudikk.navigator.sample.ui.theme.AppTheme
import kotlinx.parcelize.Parcelize

@Parcelize
class DetailsKey(val item: String) : NavigationKey

@Parcelize
class DetailsDialogKey(val item: String) : NavigationKey

@Parcelize
class DetailsBottomSheetKey(val item: String) : NavigationKey

@Parcelize
class DynamicDetailsKey(val item: String) : NavigationKey

@Parcelize
class DetailsSimpleKey(val item: String) : SimpleNavigationKey<Dialog> {

    override fun navigationNode() = dialogNode {
        DetailsScreen(item = item, isScreen = false)
    }
}

fun NavigatorRulesBuilder.detailsNavigation(screenWidth: Int) {
    if (screenWidth <= 600) {
        dialog<DynamicDetailsKey>(
            dialogOptions = DialogOptions(modifier = Modifier.widthIn(max = 320.dp))
        ) { DetailsScreen(item = it.item, isScreen = false) }
    } else {
        screen<DynamicDetailsKey> { DetailsScreen(item = it.item, isScreen = true) }
    }

    screen<DetailsKey> { key -> DetailsScreen(item = key.item, isScreen = true) }

    dialog<DetailsDialogKey>(
        dialogOptions = DialogOptions(modifier = Modifier.widthIn(max = 320.dp))
    ) { key -> DetailsScreen(item = key.item, isScreen = false) }

    bottomSheet<DetailsBottomSheetKey> { key -> DetailsScreen(item = key.item, isScreen = false) }

    transition<DetailsBottomSheetKey> { -> NavigationTransition.None }
    transition<DetailsDialogKey> { -> CrossFadeTransition }
    transition<DynamicDetailsKey> { -> CrossFadeTransition }
    transition<DetailsSimpleKey> { -> NavigationTransition.None }
}

@Composable
private fun DetailsScreen(
    item: String,
    isScreen: Boolean
) {
    val viewModel: DetailsViewModel = viewModel { DetailsViewModel(item) }

    DetailsContent(
        item = viewModel.item,
        isScreen = isScreen,
        onBackSelected = viewModel::onBackSelected,
        onRandomItemSelected = viewModel::onRandomItemSelected,
        onDynamicSelected = viewModel::onDynamicSelected,
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
    onBackSelected: () -> Unit = {},
    onRandomItemSelected: () -> Unit = {},
    onDynamicSelected: () -> Unit = {},
    onSendResultSelected: () -> Unit = {},
    onBottomSheetSelected: () -> Unit = {},
    onNewSingleInstanceSelected: () -> Unit = {},
    onExistingSingleInstanceSelected: () -> Unit = {},
    onSingleTopSelected: () -> Unit = {},
    onSingleTopBottomSheetSelected: () -> Unit = {},
    onReplaceSelected: () -> Unit = {},
    onOpenDialogSelected: () -> Unit = {}
) {
    val content = remember {
        movableContentOf {
            DetailsList(
                item = item,
                onRandomItemSelected = onRandomItemSelected,
                onDynamicSelected = onDynamicSelected,
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
            ) { content() }
        }
    } else {
        BottomSheetSurface(content = content)
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
    )
}

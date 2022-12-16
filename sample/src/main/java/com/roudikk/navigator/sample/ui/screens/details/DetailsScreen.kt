package com.roudikk.navigator.sample.ui.screens.details

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
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.roudikk.navigator.core.DialogOptions
import com.roudikk.navigator.core.NavigationKey
import com.roudikk.navigator.core.NavigatorRulesBuilder
import com.roudikk.navigator.sample.navigation.CrossFadeTransition
import com.roudikk.navigator.sample.ui.composables.SampleSurfaceContainer
import kotlinx.parcelize.Parcelize

@JvmInline
@Parcelize
value class DetailsResult(val value: String) : Parcelable

@Parcelize
class DetailsKey(val item: String) : NavigationKey

@Parcelize
class DetailsDialogKey(val item: String) : NavigationKey

@Parcelize
class DetailsBottomSheetKey(val item: String) : NavigationKey

@Parcelize
class DynamicDetailsKey(val item: String) : NavigationKey


fun NavigatorRulesBuilder.detailsNavigation(screenWidth: Int) {
    if (screenWidth <= 600) {
        dialog<DynamicDetailsKey>(
            dialogOptions = DialogOptions(modifier = Modifier.widthIn(max = 320.dp))
        ) {
            SampleSurfaceContainer { DetailsContent(item = it.item) }
        }
    } else {
        bottomSheet<DynamicDetailsKey> { DetailsContent(item = it.item) }
    }

    screen<DetailsKey> { DetailsScaffold(item = it.item) }

    dialog<DetailsDialogKey>(
        dialogOptions = DialogOptions(modifier = Modifier.widthIn(max = 320.dp))
    ) { key ->
        SampleSurfaceContainer { DetailsContent(item = key.item) }
    }

    bottomSheet<DetailsBottomSheetKey> { key -> DetailsContent(item = key.item) }

    transition<DetailsBottomSheetKey> { -> CrossFadeTransition }
    transition<DetailsDialogKey> { -> CrossFadeTransition }
    transition<DynamicDetailsKey> { -> CrossFadeTransition }
}

@Composable
private fun DetailsScaffold(
    item: String
) {
    val viewModel = viewModel { DetailsViewModel(item) }
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = { Text(text = "Details") },
                scrollBehavior = scrollBehavior,
                navigationIcon = {
                    IconButton(onClick = viewModel::onBackSelected) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
            )
        },
    ) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            DetailsContent(
                item = item,
                viewModel = viewModel
            )
        }
    }
}

@Composable
private fun DetailsContent(
    item: String,
    viewModel: DetailsViewModel = viewModel { DetailsViewModel(item) },
) {
    DetailsEventEffect(viewModel = viewModel)

    DetailsList(
        item = item,
        onRandomItemSelected = viewModel::onRandomItemSelected,
        onDynamicSelected = viewModel::onDynamicSelected,
        onSendResultSelected = viewModel::onSendResultSelected,
        onBottomSheetSelected = viewModel::onBottomSheetSelected,
        onNewSingleInstanceSelected = viewModel::onNewSingleInstanceSelected,
        onExistingSingleInstanceSelected = viewModel::onExistingSingleInstanceSelected,
        onSingleTopSelected = viewModel::onSingleTopSelected,
        onSingleTopBottomSheetSelected = viewModel::onSingleTopBottomSheetSelected,
        onReplaceSelected = viewModel::onReplaceSelected,
        onOpenDialogSelected = viewModel::onOpenDialogSelected,
        onOpenBlockingBottomSheet = viewModel::onOpenBlockingBottomSheet
    )
}

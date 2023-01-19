package com.roudikk.navigator.sample.feature.details

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun DetailsScaffold(
    item: String
) {
    val viewModel = viewModel { DetailsViewModel(item) }
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        contentWindowInsets = WindowInsets(0.dp),
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
        }
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
internal fun DetailsContent(
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
        onOpenBlockingBottomSheet = viewModel::onOpenBlockingBottomSheet,
        onOverrideScreenTransitionSelected = viewModel::onOverrideScreenTransitionSelected
    )
}

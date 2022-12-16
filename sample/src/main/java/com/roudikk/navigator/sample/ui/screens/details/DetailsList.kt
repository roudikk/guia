package com.roudikk.navigator.sample.ui.screens.details

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.roudikk.navigator.sample.ui.theme.AppTheme
import kotlinx.coroutines.delay

@Composable
fun DetailsList(
    item: String,
    onRandomItemSelected: () -> Unit = {},
    onDynamicSelected: () -> Unit = {},
    onSendResultSelected: () -> Unit = {},
    onBottomSheetSelected: () -> Unit = {},
    onNewSingleInstanceSelected: () -> Unit = {},
    onExistingSingleInstanceSelected: () -> Unit = {},
    onSingleTopSelected: () -> Unit = {},
    onSingleTopBottomSheetSelected: () -> Unit = {},
    onReplaceSelected: () -> Unit = {},
    onOpenDialogSelected: () -> Unit = {},
    onOpenBlockingBottomSheet: () -> Unit = {},
    onOverrideScreenTransitionSelected: () -> Unit = {}
) {

    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        var counter by rememberSaveable { mutableStateOf(0) }

        LaunchedEffect(key1 = Unit) {
            while (true) {
                delay(1000)
                counter++
            }
        }

        Text(
            text = "Item: $item, $counter",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.size(16.dp))

        DetailsAction(
            title = "New random item",
            onClick = onRandomItemSelected
        )

        DetailsAction(
            title = "New dynamic item",
            onClick = onDynamicSelected
        )

        DetailsAction(
            title = "Send result back to home",
            onClick = onSendResultSelected
        )

        DetailsAction(
            title = "BottomSheet",
            onClick = onBottomSheetSelected
        )

        DetailsAction(
            title = "Single top Screen",
            onClick = onSingleTopSelected

        )

        DetailsAction(
            title = "Single top bottom sheet",
            onClick = onSingleTopBottomSheetSelected
        )

        DetailsAction(
            title = "Single Instance (New)",
            onClick = onNewSingleInstanceSelected
        )

        DetailsAction(
            title = "Single Instance (Existing)",
            onClick = onExistingSingleInstanceSelected
        )

        DetailsAction(
            title = "Navigate and pop last (Replace)",
            onClick = onReplaceSelected
        )

        DetailsAction(
            title = "Open Dialog",
            onClick = onOpenDialogSelected
        )

        DetailsAction(
            title = "Open Blocking BottomSheet",
            onClick = onOpenBlockingBottomSheet
        )

        DetailsAction(
            title = "Override screen transition",
            onClick = onOverrideScreenTransitionSelected
        )
    }
}

@Composable
private fun DetailsAction(title: String, onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .widthIn(min = 300.dp),
        onClick = onClick,
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(4.dp),
        tonalElevation = 8.dp
    ) {
        Box(
            modifier = Modifier
                .padding(10.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
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
    DetailsList(item = "Test Item!")
}

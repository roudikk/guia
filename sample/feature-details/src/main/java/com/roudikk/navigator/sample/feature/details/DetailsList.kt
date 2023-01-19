package com.roudikk.navigator.sample.feature.details

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
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.roudikk.navigator.sample.feature.common.theme.AppTheme
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
            .padding(vertical = 16.dp, horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        var counter by rememberSaveable(key = "counter") { mutableStateOf(0) }

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
            title = "Screen: navigate",
            onClick = onRandomItemSelected
        )

        DetailsAction(
            title = "Screen: Single Top",
            onClick = onSingleTopSelected
        )

        DetailsAction(
            title = "Screen: Replace",
            onClick = onReplaceSelected
        )

        DetailsAction(
            title = "Screen: Single Instance (New)",
            onClick = onNewSingleInstanceSelected
        )

        DetailsAction(
            title = "Screen: Single Instance (Existing)",
            onClick = onExistingSingleInstanceSelected
        )

        DetailsAction(
            title = "Dynamic: Dialog < 600dp <= Bottom Sheet",
            onClick = onDynamicSelected
        )

        DetailsAction(
            title = "BottomSheet: Navigate",
            onClick = onBottomSheetSelected
        )

        DetailsAction(
            title = "Bottom Sheet: Single Top",
            onClick = onSingleTopBottomSheetSelected
        )

        DetailsAction(
            title = "BottomSheet: Blocking",
            onClick = onOpenBlockingBottomSheet
        )

        DetailsAction(
            title = "Dialog: Navigate",
            onClick = onOpenDialogSelected
        )

        DetailsAction(
            title = "Override screen transition",
            onClick = onOverrideScreenTransitionSelected
        )

        DetailsAction(
            title = "Send Result To Home",
            onClick = onSendResultSelected
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DetailsAction(title: String, onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .widthIn(min = 350.dp),
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
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
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

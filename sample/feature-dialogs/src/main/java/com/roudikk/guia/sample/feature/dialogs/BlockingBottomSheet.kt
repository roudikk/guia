package com.roudikk.guia.sample.feature.dialogs

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.roudikk.guia.backstack.NavBackHandler
import com.roudikk.guia.core.BottomSheet
import com.roudikk.guia.extensions.localBottomSheet
import com.roudikk.guia.sample.feature.common.composables.SampleSurfaceContainer
import com.roudikk.guia.sample.feature.common.theme.AppTheme

@Composable
internal fun BlockingBottomSheetScreen() {
    val bottomSheet = localBottomSheet()
    var lockStateChange by rememberSaveable { mutableStateOf(true) }

    LaunchedEffect(lockStateChange) {
        bottomSheet?.bottomSheetOptions = BottomSheet.BottomSheetOptions(
            confirmStateChange = { !lockStateChange }
        )
    }

    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
    ) {
        Text(text = "You can't navigate away by clicking outside this bottom sheet or swiping it down.")

        Spacer(modifier = Modifier.size(16.dp))

        Text(
            text = "Toggle the switch to enable/disable state change lock."
        )

        Spacer(modifier = Modifier.size(16.dp))

        Switch(
            modifier = Modifier.testTag("toggle"),
            checked = lockStateChange,
            onCheckedChange = { lockStateChange = it }
        )
    }

    NavBackHandler(enabled = lockStateChange) {
        // Lock back button
    }
}

@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    device = Devices.PIXEL_3
)
@Preview(
    device = Devices.PIXEL_3
)
@Composable
private fun BlockingBottomSheetContentPreview() = AppTheme {
    SampleSurfaceContainer {
        BlockingBottomSheetScreen()
    }
}

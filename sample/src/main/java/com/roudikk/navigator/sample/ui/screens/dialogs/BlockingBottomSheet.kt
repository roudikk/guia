package com.roudikk.navigator.sample.ui.screens.dialogs

import android.content.res.Configuration
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.roudikk.navigator.core.BottomSheet
import com.roudikk.navigator.core.BottomSheetOptions
import com.roudikk.navigator.sample.ui.composables.BottomSheetSurface
import com.roudikk.navigator.sample.ui.theme.AppTheme
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

@Parcelize
class BlockingBottomSheet : BottomSheet {

    @IgnoredOnParcel
    private var sheetOptions: BottomSheetOptions = BottomSheetOptions(
        modifier = Modifier.navigationBarsPadding(),
        confirmStateChange = { false },
    )

    override val bottomSheetOptions: BottomSheetOptions
        get() = sheetOptions

    @Composable
    override fun Content() = BlockingBottomSheetContent { confirm ->
        sheetOptions = sheetOptions.copy(confirmStateChange = { !confirm })
    }
}

@Composable
private fun BlockingBottomSheetContent(
    onToggle: (Boolean) -> Unit
) {
    var lockBack by rememberSaveable { mutableStateOf(true) }

    Column(
        Modifier
            .padding(16.dp)
            .fillMaxWidth()
    ) {
        Text(text = "You can't navigate away by clicking outside this bottom sheet.")

        Spacer(modifier = Modifier.size(16.dp))

        Text(
            text = "Only thing you can do is hit the back button, but that won't go back to the dialogs screen" +
                " if the below switch is turned on. Toggle it on/off to enable/disable back press."
        )

        Spacer(modifier = Modifier.size(16.dp))

        Switch(checked = lockBack, onCheckedChange = { lockBack = it })

        Spacer(modifier = Modifier.navigationBarsPadding())
    }

    BackHandler(enabled = lockBack) {
        // Lock back button
    }

    LaunchedEffect(lockBack) {
        onToggle(lockBack)
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
    BottomSheetSurface {
        BlockingBottomSheetContent { }
    }
}

package com.roudikk.navigator.sample.ui.screens.dialogs

import android.content.res.Configuration
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.accompanist.insets.navigationBarsPadding
import com.roudikk.navigator.BottomSheet
import com.roudikk.navigator.BottomSheetOptions
import com.roudikk.navigator.sample.AppPreview
import kotlinx.parcelize.Parcelize

@Parcelize
class BlockingBottomSheet : BottomSheet {

    override val bottomSheetOptions: BottomSheetOptions
        get() = BottomSheetOptions(false)

    @Composable
    override fun AnimatedVisibilityScope.Content() {
        BlockingBottomSheetContent()
    }
}

@Composable
private fun BlockingBottomSheetContent() {
    var lockBack by rememberSaveable { mutableStateOf(true) }

    Column(Modifier.padding(16.dp)) {
        Text(text = "You can't navigate away by clicking outside this bottom sheet.")

        Spacer(modifier = Modifier.size(16.dp))

        Text(
            text = "Only thing you can do is hit the back button, but that won't go back to the dialogs screen"
                    + " if the below switch is turned on. Toggle it on/off and see what happens!"
        )

        Spacer(modifier = Modifier.size(16.dp))

        Text(
            text = "This is currently not working properly until Compose 1.1.0-rc02 is released. (Or any version after 1.1.0-rc01)",
            fontWeight = FontWeight.Bold,
            color = Color(0xFFD37575)
        )

        Spacer(modifier = Modifier.size(16.dp))

        Switch(checked = lockBack, onCheckedChange = { lockBack = it })

        Spacer(modifier = Modifier.navigationBarsPadding())
    }

    BackHandler(enabled = lockBack) {
        // Lock back button
    }
}

@Preview(
    device = Devices.PIXEL_3
)
@Composable
private fun BlockingBottomSheetContentPreview() = AppPreview {
    BlockingBottomSheetContent()
}

@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    device = Devices.PIXEL_3
)
@Composable
private fun BlockingBottomSheetContentPreviewDark() = AppPreview {
    BlockingBottomSheetContent()
}
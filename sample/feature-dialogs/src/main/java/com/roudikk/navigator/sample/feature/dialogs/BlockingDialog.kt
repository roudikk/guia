package com.roudikk.navigator.sample.feature.dialogs

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
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
import com.roudikk.navigator.extensions.navigate
import com.roudikk.navigator.extensions.popBackstack
import com.roudikk.navigator.extensions.requireDialog
import com.roudikk.navigator.extensions.requireNavigator
import com.roudikk.navigator.sample.feature.common.theme.AppTheme
import com.roudikk.navigator.sample.feature.dialogs.api.CancelableDialogKey

@Composable
internal fun BlockingDialogScreen(
    showNextButton: Boolean
) {
    val navigator = requireNavigator()

    BlockingDialogContent(
        showNextButton = showNextButton,
        onNextClicked = { navigator.navigate(CancelableDialogKey(true)) },
        onCancelClicked = navigator::popBackstack
    )
}

@Composable
private fun BlockingDialogContent(
    showNextButton: Boolean,
    onNextClicked: () -> Unit = {},
    onCancelClicked: () -> Unit = {}
) {
    val dialog = requireDialog()
    var dismissDialog by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(dismissDialog) {
        dialog.dialogOptions = dialog.dialogOptions.copy(
            dismissOnBackPress = dismissDialog,
            dismissOnClickOutside = dismissDialog
        )
    }

    Surface(shape = RoundedCornerShape(16.dp)) {

        Column(modifier = Modifier.padding(16.dp)) {

            Text(
                text = "None Cancelable",
                style = MaterialTheme.typography.headlineSmall
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "This dialog cannot be cancelled by clicking outside or " +
                    "pressing the back button, unless you toggle the switch below."
            )

            Spacer(modifier = Modifier.height(10.dp))

            Switch(
                checked = dismissDialog,
                onCheckedChange = { dismissDialog = it }
            )

            Spacer(modifier = Modifier.height(10.dp))

            if (showNextButton) {
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = onNextClicked
                ) {
                    Text(text = "Next")
                }
            } else {
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = onCancelClicked
                ) {
                    Text(text = "Cancel")
                }
            }
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
private fun BlockingDialogContentPreview() = AppTheme {
    BlockingDialogContent(
        showNextButton = true
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
private fun BlockingDialogContentPreviewCancel() = AppTheme {
    BlockingDialogContent(
        showNextButton = false
    )
}

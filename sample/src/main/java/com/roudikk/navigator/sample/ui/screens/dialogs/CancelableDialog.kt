package com.roudikk.navigator.sample.ui.screens.dialogs

import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.roudikk.navigator.Dialog
import com.roudikk.navigator.Navigator
import com.roudikk.navigator.findNavigator
import com.roudikk.navigator.sample.ui.theme.AppTheme
import kotlinx.parcelize.Parcelize

@Parcelize
class CancelableDialog(
    private val showNextButton: Boolean
) : Dialog {

    @Composable
    override fun AnimatedVisibilityScope.Content() {
        CancelableDialogContent(showNextButton = showNextButton)
    }
}

@Composable
private fun CancelableDialogContent(
    navigator: Navigator = findNavigator(),
    showNextButton: Boolean
) {
    Surface(
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(
                text = "Cancelable Dialog",
                style = MaterialTheme.typography.headlineSmall
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "This dialog is cancelable so you can dismiss it by" +
                        " clicking outside or by " +
                        "pressing the back button."
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Content of a dialog can be anything, so no" +
                        " dialog buttons are provided"
            )

            if (showNextButton) {
                Spacer(modifier = Modifier.height(10.dp))

                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        navigator.popToRoot()
                    }
                ) {
                    Text(text = "Go back to root")
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
private fun CancelableDialogContentPreview() = AppTheme {
    CancelableDialogContent(
        navigator = Navigator(),
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
private fun CancelableDialogContentPreviewFalse() = AppTheme {
    CancelableDialogContent(
        navigator = Navigator(),
        showNextButton = false
    )
}

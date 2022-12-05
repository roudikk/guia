package com.roudikk.navigator.sample.ui.screens.dialogs

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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.roudikk.navigator.core.NavigationKey
import com.roudikk.navigator.NavigatorRulesBuilder
import com.roudikk.navigator.compose.requireNavigator
import com.roudikk.navigator.popToRoot
import com.roudikk.navigator.sample.ui.theme.AppTheme
import kotlinx.parcelize.Parcelize

@Parcelize
data class CancelableDialogKey(val showNextButton: Boolean) : NavigationKey

fun NavigatorRulesBuilder.cancelableDialogNavigation() {
    dialog<CancelableDialogKey> {
        CancelableDialogScreen(showNextButton = it.showNextButton)
    }
}

@Composable
private fun CancelableDialogScreen(
    showNextButton: Boolean
) {
    val navigator = requireNavigator()
    CancelableDialogContent(
        showNextButton = showNextButton,
        onBackToRootClicked = navigator::popToRoot
    )
}

@Composable
private fun CancelableDialogContent(
    showNextButton: Boolean,
    onBackToRootClicked: () -> Unit = {}
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
                    onClick = onBackToRootClicked
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
        showNextButton = false
    )
}

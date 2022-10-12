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
import com.roudikk.navigator.Navigator
import com.roudikk.navigator.compose.requireNavigator
import com.roudikk.navigator.core.Dialog
import com.roudikk.navigator.core.DialogOptions
import com.roudikk.navigator.rememberNavigator
import com.roudikk.navigator.sample.navigation.CrossFadeTransition
import com.roudikk.navigator.sample.ui.theme.AppTheme
import kotlinx.parcelize.Parcelize

@Parcelize
class BlockingDialog(private val showNextButton: Boolean) : Dialog {

    override val dialogOptions: DialogOptions
        get() = DialogOptions(
            dismissOnBackPress = false,
            dismissOnClickOutside = false
        )

    @Composable
    override fun Content() = BlockingDialogContent(showNextButton = showNextButton)
}

@Composable
private fun BlockingDialogContent(
    navigator: Navigator = requireNavigator(),
    showNextButton: Boolean
) {
    Surface(shape = RoundedCornerShape(16.dp)) {

        Column(Modifier.padding(16.dp)) {
            Text(
                text = "None Cancelable",
                style = MaterialTheme.typography.headlineSmall
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "This dialog cannot be cancelled by clicking outside or " +
                        "pressing the back button."
            )

            Spacer(modifier = Modifier.height(10.dp))

            if (showNextButton) {
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        navigator.navigate(
                            navigationNode = CancelableDialog(true),
                            transition = CrossFadeTransition
                        )
                    }
                ) {
                    Text(text = "Next")
                }
            } else {
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        navigator.popBackStack()
                    }
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
        navigator = rememberNavigator(),
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
        navigator = rememberNavigator(),
        showNextButton = false
    )
}

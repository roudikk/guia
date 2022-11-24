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
import com.roudikk.navigator.NavigationKey
import com.roudikk.navigator.Navigator
import com.roudikk.navigator.NavigatorRulesScope
import com.roudikk.navigator.compose.requireNavigator
import com.roudikk.navigator.core.DialogOptions
import com.roudikk.navigator.navigate
import com.roudikk.navigator.popBackStack
import com.roudikk.navigator.sample.ui.theme.AppTheme
import kotlinx.parcelize.Parcelize

@Parcelize
data class BlockingDialogKey(val showNextButton: Boolean) : NavigationKey

fun NavigatorRulesScope.blockingDialogNavigation() {
    dialog<BlockingDialogKey>(
        DialogOptions(dismissOnBackPress = false, dismissOnClickOutside = false)
    ) {
        BlockingDialogScreen(showNextButton = it.showNextButton)
    }
}

@Composable
private fun BlockingDialogScreen(
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
                        navigator.navigate(CancelableDialogKey(true))
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
//    BlockingDialogScreen(
//        navigator = rememberNavigator(),
//        showNextButton = true
//    )
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
//    BlockingDialogScreen(
//        navigator = rememberNavigator(),
//        showNextButton = false
//    )
}

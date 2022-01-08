package com.roudikk.composenavigator.ui.screens.dialogs

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.roudikk.compose_navigator.Dialog
import com.roudikk.compose_navigator.DialogOptions
import com.roudikk.compose_navigator.findNavigator
import com.roudikk.composenavigator.AppPreview
import kotlinx.parcelize.Parcelize

@Parcelize
@OptIn(ExperimentalMaterialApi::class)
class BlockingDialog(
    private val showButton: Boolean
) : Dialog {

    override val dialogOptions: DialogOptions
        get() = DialogOptions(
            dismissOnBackPress = false,
            dismissOnClickOutside = false
        )

    @Composable
    override fun Content(animatedVisibilityScope: AnimatedVisibilityScope) {
        BlockingDialogContent(showButton)
    }
}

@Composable
private fun BlockingDialogContent(showButton: Boolean) {
    val navigator = findNavigator()

    Surface(
        shape = RoundedCornerShape(16.dp)
    ) {
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

            if (showButton) {
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        navigator.navigate(CancelableDialog(true))
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


@Preview
@Composable
private fun BlockingDialogContentPreview() = AppPreview {
    BlockingDialogContent(showButton = true)
}

@Preview
@Composable
private fun BlockingDialogContentPreviewFalse() = AppPreview {
    BlockingDialogContent(showButton = false)
}
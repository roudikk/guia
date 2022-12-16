package com.roudikk.navigator.compose.containers

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.with
import androidx.compose.runtime.Composable
import androidx.compose.ui.window.Dialog
import com.roudikk.navigator.compose.backstack.BackStackEntry
import com.roudikk.navigator.core.Dialog
import com.roudikk.navigator.core.Navigator
import com.roudikk.navigator.core.toDialogProperties
import com.roudikk.navigator.extensions.popBackstack

@OptIn(ExperimentalAnimationApi::class)
@Composable
internal fun Navigator.DialogContainer(
    dialogEntry: BackStackEntry,
    content: @Composable AnimatedVisibilityScope.(BackStackEntry) -> Unit
) {
    val dialog = navigationNode(dialogEntry.destination) as Dialog

    Dialog(
        onDismissRequest = { popBackstack() },
        properties = dialog.dialogOptions.toDialogProperties()
    ) {
        AnimatedContent(
            targetState = dialogEntry,
            modifier = dialog.dialogOptions.modifier,
            transitionSpec = {
                transition.enter with transition.exit
            }
        ) { dialogEntry ->
            content(dialogEntry)
        }
    }
}

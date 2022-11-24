package com.roudikk.navigator.compose.containers

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.with
import androidx.compose.runtime.Composable
import androidx.compose.ui.window.Dialog
import com.roudikk.navigator.Navigator
import com.roudikk.navigator.animation.NavEnterExitTransition
import com.roudikk.navigator.compose.backstack.BackStackEntry
import com.roudikk.navigator.core.Dialog
import com.roudikk.navigator.core.toDialogProperties

@OptIn(ExperimentalAnimationApi::class)
@Composable
internal fun Navigator.DialogContainer(
    dialogEntry: BackStackEntry,
    transition: NavEnterExitTransition,
    onDismissRequest: () -> Unit,
    content: @Composable AnimatedVisibilityScope.(BackStackEntry) -> Unit
) {
    val dialog = navigationNode(dialogEntry.destination) as Dialog

    Dialog(
        onDismissRequest = onDismissRequest,
        properties = dialog.dialogOptions.toDialogProperties()
    ) {
        AnimatedContent(
            targetState = dialogEntry,
            modifier = dialog.dialogOptions.modifier,
            transitionSpec = {
                transition.enter.toComposeEnterTransition() with
                    transition.exit.toComposeExitTransition()
            }
        ) { dialogEntry ->
            content(dialogEntry)
        }
    }
}

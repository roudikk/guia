package com.roudikk.navigator.compose.containers

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.with
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.roudikk.navigator.animation.NavEnterExitTransition
import com.roudikk.navigator.compose.backstack.BackStackEntry
import com.roudikk.navigator.core.Dialog

@OptIn(
    ExperimentalComposeUiApi::class,
    ExperimentalAnimationApi::class
)
@Composable
internal fun DialogContainer(
    dialogEntry: BackStackEntry,
    transition: NavEnterExitTransition,
    onDismissRequest: () -> Unit,
    content: @Composable AnimatedVisibilityScope.(BackStackEntry) -> Unit
) {
    val dialog = dialogEntry.destination.navigationNode as Dialog

    Dialog(
        onDismissRequest = onDismissRequest,
        properties = with(dialog.dialogOptions) {
            DialogProperties(
                dismissOnBackPress = dismissOnBackPress,
                dismissOnClickOutside = dismissOnClickOutside,
                securePolicy = securePolicy,
                // This is used because there's a bug with updating content of a Dialog
                usePlatformDefaultWidth = false,
            )
        }
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

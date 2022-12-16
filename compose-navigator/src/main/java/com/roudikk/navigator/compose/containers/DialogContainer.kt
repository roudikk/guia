package com.roudikk.navigator.compose.containers

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.with
import androidx.compose.runtime.Composable
import androidx.compose.ui.window.Dialog
import com.roudikk.navigator.compose.ProvideNavigationVisibilityScope
import com.roudikk.navigator.compose.backstack.BackStackEntry
import com.roudikk.navigator.core.Dialog
import com.roudikk.navigator.Navigator
import com.roudikk.navigator.core.toDialogProperties
import com.roudikk.navigator.extensions.popBackstack

@OptIn(ExperimentalAnimationApi::class)
@Composable
internal fun Navigator.DialogContainer(
    dialogEntry: BackStackEntry,
    content: @Composable (BackStackEntry) -> Unit
) {
    val dialog = navigationNode(dialogEntry.navigationEntry) as Dialog

    Dialog(
        onDismissRequest = { popBackstack() },
        properties = dialog.dialogOptions.toDialogProperties()
    ) {
        AnimatedContent(
            targetState = dialogEntry,
            modifier = dialog.dialogOptions.modifier,
            transitionSpec = {
                currentTransition.enter with currentTransition.exit
            }
        ) { dialogEntry ->
            ProvideNavigationVisibilityScope {
                content(dialogEntry)
            }
        }
    }
}

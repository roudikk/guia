package com.roudikk.navigator.containers

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.with
import androidx.compose.runtime.Composable
import androidx.compose.ui.window.Dialog
import com.roudikk.navigator.animation.ProvideNavigationVisibilityScope
import com.roudikk.navigator.backstack.LifeCycleEntry
import com.roudikk.navigator.core.Dialog
import com.roudikk.navigator.core.Navigator
import com.roudikk.navigator.core.navigationNode
import com.roudikk.navigator.core.toDialogProperties
import com.roudikk.navigator.extensions.popBackstack

/**
 * Renders a Compose Dialog if a [Navigator]'s current entry is a [Dialog].
 */
@OptIn(ExperimentalAnimationApi::class)
@Composable
internal fun Navigator.DialogContainer(
    dialogEntry: LifeCycleEntry,
    content: @Composable (LifeCycleEntry) -> Unit
) {
    val dialog = navigationNode(dialogEntry.backStackEntry) as Dialog

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

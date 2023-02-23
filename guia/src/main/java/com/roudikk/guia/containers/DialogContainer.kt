package com.roudikk.guia.containers

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.with
import androidx.compose.runtime.Composable
import androidx.compose.ui.window.Dialog
import com.roudikk.guia.animation.ProvideNavVisibilityScope
import com.roudikk.guia.core.Dialog
import com.roudikk.guia.core.Navigator
import com.roudikk.guia.core.navigationNode
import com.roudikk.guia.core.nodeTransition
import com.roudikk.guia.core.toDialogProperties
import com.roudikk.guia.extensions.pop
import com.roudikk.guia.lifecycle.LifecycleEntry

/**
 * Renders a Compose Dialog if a [Navigator]'s current entry is a [Dialog].
 */
@OptIn(ExperimentalAnimationApi::class)
@Composable
fun Navigator.DialogContainer(
    container: Container,
    dialogEntry: LifecycleEntry?,
    content: @Composable (LifecycleEntry) -> Unit
) {
    dialogEntry ?: return

    val dialog = navigationNode(dialogEntry.backstackEntry) as Dialog

    Dialog(
        onDismissRequest = ::pop,
        properties = dialog.dialogOptions.toDialogProperties()
    ) {
        container {
            AnimatedContent(
                targetState = dialogEntry,
                transitionSpec = {
                    nodeTransition<Dialog>().let { it.enter with it.exit }
                }
            ) { dialogEntry ->
                ProvideNavVisibilityScope {
                    content(dialogEntry)
                }
            }
        }
    }
}

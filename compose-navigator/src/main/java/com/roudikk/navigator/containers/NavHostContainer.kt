package com.roudikk.navigator.containers

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.with
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.roudikk.navigator.extensions.LocalNavHost
import com.roudikk.navigator.navhost.NavHost
import com.roudikk.navigator.navhost.StackEntry
import com.roudikk.navigator.navhost.StackKey

internal typealias StackKeyContainer = @Composable (
    stackKey: StackKey,
    content: @Composable () -> Unit
) -> Unit

/**
 * Renders the current state of a [NavHost].
 *
 * @param modifier, [Modifier] for the [NavHost.currentNavigator]'s [NavContainer].
 * @param transitionSpec, defines the transition between the stack entries.
 * @param bottomSheetSetup, will be provided to the [NavHost.currentNavigator]'s [NavContainer].
 */
@OptIn(ExperimentalAnimationApi::class)
@Composable
fun NavHost.NavContainer(
    modifier: (StackKey) -> Modifier = { Modifier },
    bottomSheetScrimColor: @Composable (StackKey) -> Color = {
        MaterialTheme.colors.onSurface.copy(alpha = 0.32f)
    },
    bottomSheetContainer: StackKeyContainer = { _, content -> content() },
    dialogContainer: StackKeyContainer = { _, content -> content() },
    transitionSpec: AnimatedContentScope<StackEntry?>.() -> ContentTransform = {
        EnterTransition.None with ExitTransition.None
    }
) {
    CompositionLocalProvider(LocalNavHost provides this) {
        AnimatedContent(
            targetState = currentEntry,
            transitionSpec = transitionSpec
        ) { targetEntry ->
            targetEntry?.let {
                saveableStateHolder.SaveableStateProvider(it.stackKey) {
                    targetEntry.navigator.NavContainer(
                        modifier = modifier(targetEntry.stackKey),
                        bottomSheetScrimColor = bottomSheetScrimColor(targetEntry.stackKey),
                        bottomSheetContainer = { content ->
                            bottomSheetContainer(targetEntry.stackKey, content)
                        },
                        dialogContainer = { content ->
                            dialogContainer(targetEntry.stackKey, content)
                        },
                    )
                }
            }
        }
    }
}

package com.roudikk.navigator.containers

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.with
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import com.roudikk.navigator.core.BottomSheetSetup
import com.roudikk.navigator.extensions.LocalNavHost
import com.roudikk.navigator.navhost.NavHost
import com.roudikk.navigator.navhost.StackEntry
import com.roudikk.navigator.navhost.StackKey

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
    transitionSpec: AnimatedContentScope<StackEntry?>.() -> ContentTransform = {
        EnterTransition.None with ExitTransition.None
    },
    bottomSheetSetup: (StackKey) -> BottomSheetSetup
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
                        bottomSheetOptions = bottomSheetSetup(targetEntry.stackKey),
                    )
                }
            }
        }
    }
}

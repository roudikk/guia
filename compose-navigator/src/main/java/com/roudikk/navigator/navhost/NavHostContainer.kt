package com.roudikk.navigator.navhost

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.with
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.key
import androidx.compose.ui.Modifier
import com.roudikk.navigator.compose.BottomSheetSetup
import com.roudikk.navigator.compose.NavContainer
import com.roudikk.navigator.core.StackKey

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

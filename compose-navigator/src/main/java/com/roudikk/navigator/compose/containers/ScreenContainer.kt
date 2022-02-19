package com.roudikk.navigator.compose.containers

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.with
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.roudikk.navigator.animation.NavEnterExitTransition
import com.roudikk.navigator.compose.backstack.BackStackEntry

@OptIn(ExperimentalAnimationApi::class)
@Composable
internal fun ScreenContainer(
    modifier: Modifier,
    transition: NavEnterExitTransition,
    screenEntry: BackStackEntry?,
    content: @Composable AnimatedVisibilityScope.(BackStackEntry) -> Unit
) {
    AnimatedContent(
        targetState = screenEntry,
        modifier = Modifier
            .fillMaxSize()
            .then(modifier),
        transitionSpec = {
            transition.enter.toComposeEnterTransition() with
                transition.exit.toComposeExitTransition()
        }
    ) { backStackEntry ->
        backStackEntry?.let {
            content(backStackEntry)
        }
    }
}

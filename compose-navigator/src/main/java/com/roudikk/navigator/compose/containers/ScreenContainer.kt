package com.roudikk.navigator.compose.containers

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.with
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.roudikk.navigator.compose.ProvideNavigationVisibilityScope
import com.roudikk.navigator.compose.backstack.BackStackEntry
import com.roudikk.navigator.core.Navigator

@OptIn(ExperimentalAnimationApi::class)
@Composable
internal fun Navigator.ScreenContainer(
    modifier: Modifier,
    screenEntry: BackStackEntry?,
    content: @Composable (BackStackEntry) -> Unit
) {
    AnimatedContent(
        targetState = screenEntry,
        modifier = Modifier
            .fillMaxSize()
            .then(modifier),
        transitionSpec = {
            transition.enter with transition.exit
        }
    ) { backStackEntry ->
        backStackEntry?.let {
            ProvideNavigationVisibilityScope {
                content(backStackEntry)
            }
        }
    }
}

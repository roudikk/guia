package com.roudikk.navigator.containers

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.with
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.roudikk.navigator.animation.ProvideNavigationVisibilityScope
import com.roudikk.navigator.backstack.LifeCycleEntry
import com.roudikk.navigator.core.Navigator
import com.roudikk.navigator.core.Screen

/**
 * Renders the content of a screen if a [Navigator]'s current entry is a [Screen].
 */
@OptIn(ExperimentalAnimationApi::class)
@Composable
internal fun Navigator.ScreenContainer(
    modifier: Modifier,
    screenEntry: LifeCycleEntry?,
    content: @Composable (LifeCycleEntry) -> Unit
) {
    AnimatedContent(
        targetState = screenEntry,
        modifier = Modifier
            .fillMaxSize()
            .then(modifier),
        transitionSpec = {
            currentTransition.enter with currentTransition.exit
        }
    ) { backStackEntry ->
        backStackEntry?.let {
            ProvideNavigationVisibilityScope {
                content(backStackEntry)
            }
        }
    }
}

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
import com.roudikk.navigator.core.transition

/**
 * Renders the content of a screen if a [Navigator]'s current entry is a [Screen].
 */
@OptIn(ExperimentalAnimationApi::class)
@Composable
fun Navigator.ScreenContainer(
    screenEntry: LifeCycleEntry?,
    content: @Composable (LifeCycleEntry) -> Unit
) {
    AnimatedContent(
        targetState = screenEntry,
        modifier = Modifier
            .fillMaxSize(),
        transitionSpec = {
            transition<Screen>().let { it.enter with it.exit }
        }
    ) { backStackEntry ->
        backStackEntry?.let {
            ProvideNavigationVisibilityScope {
                content(backStackEntry)
            }
        }
    }
}

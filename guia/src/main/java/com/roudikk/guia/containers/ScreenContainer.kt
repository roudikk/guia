package com.roudikk.guia.containers

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.with
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.roudikk.guia.animation.ProvideNavVisibilityScope
import com.roudikk.guia.core.Navigator
import com.roudikk.guia.core.Screen
import com.roudikk.guia.core.keyTransition
import com.roudikk.guia.lifecycle.LifecycleEntry

/**
 * Renders the content of a screen if a [Navigator]'s current entry is a [Screen].
 */
@OptIn(ExperimentalAnimationApi::class)
@Composable
fun Navigator.ScreenContainer(
    screenEntry: LifecycleEntry?,
    content: @Composable (LifecycleEntry) -> Unit
) {
    AnimatedContent(
        targetState = screenEntry,
        modifier = Modifier
            .fillMaxSize(),
        transitionSpec = {
            keyTransition<Screen>().let { it.enter with it.exit }
        }
    ) { backstackEntry ->
        backstackEntry?.let {
            ProvideNavVisibilityScope {
                content(backstackEntry)
            }
        }
    }
}

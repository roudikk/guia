package com.roudikk.guia.containers

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.with
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.roudikk.guia.animation.ProvideNavigationVisibilityScope
import com.roudikk.guia.backstack.LifecycleEntry
import com.roudikk.guia.core.Navigator
import com.roudikk.guia.core.Screen
import com.roudikk.guia.core.transition

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
            transition<Screen>().let { it.enter with it.exit }
        }
    ) { backstackEntry ->
        backstackEntry?.let {
            ProvideNavigationVisibilityScope {
                content(backstackEntry)
            }
        }
    }
}

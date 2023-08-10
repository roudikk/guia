package com.roudikk.guia.containers

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import com.roudikk.guia.animation.ProvideNavVisibilityScope
import com.roudikk.guia.core.Navigator
import com.roudikk.guia.core.Screen
import com.roudikk.guia.core.nodeTransition
import com.roudikk.guia.lifecycle.LifecycleEntry

/**
 * Renders the content of a screen if a [Navigator]'s current entry is a [Screen].
 */
@Composable
fun Navigator.ScreenContainer(
    screenEntry: LifecycleEntry?,
    content: @Composable (LifecycleEntry) -> Unit
) {
    AnimatedContent(
        label = "NavigationScreenContainer_entry",
        targetState = screenEntry,
        modifier = Modifier
            .then(
                screenEntry?.let {
                    Modifier.testTag("screen_container")
                } ?: Modifier
            )
            .fillMaxSize(),
        transitionSpec = {
            nodeTransition<Screen>().let { it.enter togetherWith it.exit }
        }
    ) { backstackEntry ->
        backstackEntry?.let {
            ProvideNavVisibilityScope {
                content(backstackEntry)
            }
        }
    }
}

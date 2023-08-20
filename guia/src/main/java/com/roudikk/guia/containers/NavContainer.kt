package com.roudikk.guia.containers

import androidx.compose.foundation.layout.Box
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.roudikk.guia.backstack.NavBackHandler
import com.roudikk.guia.core.Navigator
import com.roudikk.guia.core.Screen
import com.roudikk.guia.extensions.LocalNavigator
import com.roudikk.guia.extensions.LocalParentNavigator
import com.roudikk.guia.extensions.canGoBack
import com.roudikk.guia.extensions.pop
import com.roudikk.guia.lifecycle.rememberDefaultLifecycleManager

internal typealias Container = @Composable (
    content: @Composable () -> Unit
) -> Unit

/**
 * [NavContainer] renders the current state of a [Navigator].
 *
 * It manages the lifecycle and state restoration of all navigation nodes rendered within this
 * container.
 *
 * @param modifier, applied to [ScreenContainer] for all [Screen] navigation nodes.
 * @param bottomSheetScrimColor, the scrim color the bottom sheet entries.
 * @param bottomSheetContainer, the container behind the bottom sheet entries.
 * @param dialogContainer, the container behind the dialog entries.
 */
@Composable
fun Navigator.NavContainer(
    modifier: Modifier = Modifier,
    bottomSheetScrimColor: Color = MaterialTheme.colors.onSurface.copy(alpha = 0.32F),
    bottomSheetContainer: Container = { content -> content() },
    dialogContainer: Container = { content -> content() }
) {
    val parentNavigator = LocalNavigator.current

    CompositionLocalProvider(
        LocalParentNavigator provides parentNavigator,
        LocalNavigator provides this
    ) {
        NavContainerContent(
            navigator = this,
            modifier = modifier,
            bottomSheetScrimColor = bottomSheetScrimColor,
            bottomSheetContainer = bottomSheetContainer,
            dialogContainer = dialogContainer
        )
    }
}

@Composable
private fun Navigator.NavContainerContent(
    navigator: Navigator,
    modifier: Modifier = Modifier,
    bottomSheetScrimColor: Color,
    bottomSheetContainer: Container,
    dialogContainer: Container,
) = Box(modifier = modifier) {
    val canGoBack by navigator.canGoBack()
    val lifecycleManager = rememberDefaultLifecycleManager(navigator = navigator)
    val renderGroup = lifecycleManager.renderGroup

    val backEnabled by remember(canGoBack, navigator.overrideBackPress) {
        derivedStateOf { canGoBack && navigator.overrideBackPress }
    }

    NavBackHandler(enabled = backEnabled) {
        navigator.pop()
    }

    // Screen content
    ScreenContainer(
        screenEntry = renderGroup.screenEntry
    ) { entry ->
        NavEntryContainer(
            lifecycleManager = lifecycleManager,
            lifecycleEntry = entry
        )
    }

    // Bottom sheet content
    BottomSheetContainer(
        bottomSheetEntry = renderGroup.bottomSheetEntry,
        bottomSheetScrimColor = bottomSheetScrimColor,
        container = bottomSheetContainer,
    ) { entry ->
        NavBackHandler(enabled = backEnabled) {
            navigator.pop()
        }

        NavEntryContainer(
            lifecycleManager = lifecycleManager,
            lifecycleEntry = entry
        )
    }

    // Dialog content
    DialogContainer(
        dialogEntry = renderGroup.dialogEntry,
        container = dialogContainer,
    ) { entry ->
        NavEntryContainer(
            lifecycleManager = lifecycleManager,
            lifecycleEntry = entry
        )
    }

    DisposableEffect(Unit) {
        onDispose(lifecycleManager::onDispose)
    }
}

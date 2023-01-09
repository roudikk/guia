package com.roudikk.navigator.containers

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.roudikk.navigator.backstack.rememberBackStackManager
import com.roudikk.navigator.core.Navigator
import com.roudikk.navigator.core.Screen
import com.roudikk.navigator.extensions.LocalNavigator
import com.roudikk.navigator.extensions.LocalParentNavigator
import com.roudikk.navigator.extensions.canGoBack
import com.roudikk.navigator.extensions.findNavigator
import com.roudikk.navigator.extensions.popBackstack

/**
 * [NavContainer] renders the current state of a [Navigator].
 *
 * It manages the lifecycle and state restoration of all navigation nodes rendered within this
 * container.
 *
 * @param modifier, applied to [ScreenContainer] for all [Screen] navigation nodes.
 */
@Composable
fun Navigator.NavContainer(
    modifier: Modifier = Modifier
) {
    val parentNavigator = findNavigator()

    CompositionLocalProvider(
        LocalParentNavigator provides parentNavigator,
        LocalNavigator provides this
    ) {
        NavContainerContent(
            navigator = this,
            modifier = modifier,
        )
    }
}

@Composable
private fun Navigator.NavContainerContent(
    modifier: Modifier = Modifier,
    navigator: Navigator
) = BoxWithConstraints(modifier = modifier) {
    val canGoBack by navigator.canGoBack()
    val backStackManager = rememberBackStackManager(navigator = navigator)
    val visibleBackStack by backStackManager.visibleBackStack

    val backEnabled by remember(canGoBack, navigator.overrideBackPress) {
        derivedStateOf { canGoBack && navigator.overrideBackPress }
    }

    BackHandler(backEnabled) {
        navigator.popBackstack()
    }
    // Screen content
    ScreenContainer(
        screenEntry = visibleBackStack.screenEntry
    ) { entry ->
        NavigationEntryContainer(backStackManager, entry)
    }

    // Bottom sheet content
    BottomSheetContainer(
        bottomSheetEntry = visibleBackStack.bottomSheetEntry,
        content = { entry ->
            BackHandler(backEnabled) {
                navigator.popBackstack()
            }

            NavigationEntryContainer(backStackManager, entry)
        }
    )

    // Dialog content
    visibleBackStack.dialogEntry?.let { dialogEntry ->
        DialogContainer(
            dialogEntry = dialogEntry
        ) { entry ->
            NavigationEntryContainer(backStackManager, entry)
        }
    }

    DisposableEffect(Unit) {
        onDispose(backStackManager::onDispose)
    }
}

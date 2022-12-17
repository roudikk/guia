package com.roudikk.navigator.compose

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.roudikk.navigator.Navigator
import com.roudikk.navigator.backstack.rememberBackStackManager
import com.roudikk.navigator.containers.BottomSheetContainer
import com.roudikk.navigator.containers.DialogContainer
import com.roudikk.navigator.containers.ScreenContainer
import com.roudikk.navigator.core.BottomSheetSetup
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
 * @param bottomSheetOptions, custom options for bottom sheets rendered within this container.
 */
@Composable
fun Navigator.NavContainer(
    modifier: Modifier = Modifier,
    bottomSheetOptions: BottomSheetSetup = BottomSheetSetup()
) {
    val parentNavigator = findNavigator()

    CompositionLocalProvider(
        LocalParentNavigator provides parentNavigator,
        LocalNavigator provides this
    ) {
        NavContainerContent(
            navigator = this,
            modifier = modifier,
            bottomSheetSetup = bottomSheetOptions
        )
    }
}

@Composable
private fun Navigator.NavContainerContent(
    modifier: Modifier = Modifier,
    navigator: Navigator,
    bottomSheetSetup: BottomSheetSetup = BottomSheetSetup()
) {
    val canGoBack by navigator.canGoBack()

    val backStackManager = rememberBackStackManager(navigator = navigator)

    val backStackEntryGroup by backStackManager.backStackEntryGroup

    val enabled by remember(canGoBack, navigator.overrideBackPress) {
        derivedStateOf { canGoBack && navigator.overrideBackPress }
    }

    BackHandler(enabled) {
        navigator.popBackstack()
    }

    // Bottom sheet content
    BottomSheetContainer(
        bottomSheetEntry = backStackEntryGroup.bottomSheetEntry,
        bottomSheetSetup = bottomSheetSetup,
        content = { entry ->
            BackHandler(enabled) {
                navigator.popBackstack()
            }

            NavigationEntryContainer(backStackManager, entry)
        }
    ) {
        // Screen content
        ScreenContainer(
            modifier = modifier,
            screenEntry = backStackEntryGroup.screenEntry
        ) { entry ->
            NavigationEntryContainer(backStackManager, entry)
        }
    }

    // Dialog content
    backStackEntryGroup.dialogEntry?.let { dialogEntry ->
        DialogContainer(
            dialogEntry = dialogEntry
        ) { entry ->
            NavigationEntryContainer(backStackManager, entry)
        }
    }

    DisposableEffect(navigator) {
        onDispose(backStackManager::onDispose)
    }
}

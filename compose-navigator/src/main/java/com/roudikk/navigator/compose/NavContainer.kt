package com.roudikk.navigator.compose

import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.roudikk.navigator.Navigator
import com.roudikk.navigator.canGoBack
import com.roudikk.navigator.compose.backstack.rememberBackStackManager
import com.roudikk.navigator.compose.containers.BottomSheetContainer
import com.roudikk.navigator.compose.containers.DialogContainer
import com.roudikk.navigator.compose.containers.ScreenContainer
import com.roudikk.navigator.core.BottomSheet
import com.roudikk.navigator.core.Screen
import com.roudikk.navigator.popBackstack

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
    val parentNavigator = findParentNavigator()

    val canGoBack by navigator.canGoBack()

    val backStackManager = rememberBackStackManager(navigator = navigator)

    val backStackEntryGroup by backStackManager.backStackEntryGroup

    val parentShowingBottomSheet by remember {
        derivedStateOf {
            parentNavigator?.destinations?.last()
                ?.let(parentNavigator::navigationNode) is BottomSheet
        }
    }

    val enabled = canGoBack && navigator.overrideBackPress && !parentShowingBottomSheet

    val backDispatcher = LocalOnBackPressedDispatcherOwner.current!!.onBackPressedDispatcher

    DisposableEffect(enabled) {
        val callback = object : OnBackPressedCallback(enabled) {
            override fun handleOnBackPressed() {
                navigator.popBackstack()
            }
        }
        backDispatcher.addCallback(callback)
        onDispose { callback.remove() }
    }

    // Bottom sheet content
    BottomSheetContainer(
        bottomSheetEntry = backStackEntryGroup.bottomSheetEntry,
        bottomSheetSetup = bottomSheetSetup,
        transition = navigator.transition,
        currentDestination = { navigator.destinations.last() },
        onSheetHidden = { navigator.popBackstack() },
        content = { entry -> NavigationEntry(backStackManager, entry) }
    ) {
        // Screen content
        ScreenContainer(
            modifier = modifier,
            transition = navigator.transition,
            screenEntry = backStackEntryGroup.screenEntry
        ) { entry ->
            NavigationEntry(backStackManager, entry)
        }
    }

    // Dialog content
    backStackEntryGroup.dialogEntry?.let { dialogEntry ->
        DialogContainer(
            dialogEntry = dialogEntry,
            transition = navigator.transition,
            onDismissRequest = { navigator.popBackstack() },
        ) { entry ->
            NavigationEntry(backStackManager, entry)
        }
    }

    DisposableEffect(navigator) {
        onDispose(backStackManager::onDispose)
    }
}

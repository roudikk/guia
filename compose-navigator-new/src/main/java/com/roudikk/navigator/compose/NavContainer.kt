package com.roudikk.navigator.compose

import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.roudikk.navigator.Navigator
import com.roudikk.navigator.compose.backstack.rememberBackStackManager
import com.roudikk.navigator.compose.containers.BottomSheetContainer
import com.roudikk.navigator.compose.containers.DialogContainer
import com.roudikk.navigator.compose.containers.ScreenContainer
import com.roudikk.navigator.core.BottomSheet
import com.roudikk.navigator.core.Screen

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
    bottomSheetOptions: BottomSheetOptions = BottomSheetOptions()
) {
    val parentNavigator = LocalNavigator.current

    CompositionLocalProvider(
        LocalParentNavigator provides parentNavigator,
        LocalNavigator provides this
    ) {
        NavContainerContent(
            navigator = this,
            modifier = modifier,
            bottomSheetOptions = bottomSheetOptions
        )
    }
}

@Composable
private fun NavContainerContent(
    modifier: Modifier = Modifier,
    navigator: Navigator,
    bottomSheetOptions: BottomSheetOptions = BottomSheetOptions()
) {
    val parentNavigator = findParentNavigator()

    val state by navigator.stateFlow.collectAsState()
    val parentState = parentNavigator?.stateFlow?.collectAsState()

    val backStackManager = rememberBackStackManager(navigator = navigator)

    val backStackEntryGroup by backStackManager.backStackEntryGroup

    val parentShowingBottomSheet by remember {
        derivedStateOf {
            parentState?.value?.currentStack?.destinations?.last()
                ?.navigationNode is BottomSheet
        }
    }

    val enabled = navigator.canGoBack() && state.overrideBackPress && !parentShowingBottomSheet

    val backDispatcher = LocalOnBackPressedDispatcherOwner.current!!.onBackPressedDispatcher

    DisposableEffect(enabled) {
        val callback = object : OnBackPressedCallback(enabled) {
            override fun handleOnBackPressed() {
                navigator.popBackStack()
            }
        }
        backDispatcher.addCallback(callback)
        onDispose { callback.remove() }
    }

    // Bottom sheet content
    BottomSheetContainer(
        bottomSheetEntry = backStackEntryGroup.bottomSheetEntry,
        bottomSheetOptions = bottomSheetOptions,
        transition = state.transition,
        currentDestination = { navigator.currentState.currentStack.destinations.last() },
        onSheetHidden = { navigator.popBackStack() },
        content = { entry -> NavigationEntry(backStackManager, entry) }
    ) {
        // Screen content
        ScreenContainer(
            modifier = modifier,
            transition = state.transition,
            screenEntry = backStackEntryGroup.screenEntry
        ) { entry ->
            NavigationEntry(backStackManager, entry)
        }
    }

    // Dialog content
    backStackEntryGroup.dialogEntry?.let { dialogEntry ->
        DialogContainer(
            dialogEntry = dialogEntry,
            transition = state.transition,
            onDismissRequest = { navigator.popBackStack() },
        ) { entry ->
            NavigationEntry(backStackManager, entry)
        }
    }

    DisposableEffect(navigator) {
        onDispose(backStackManager::onDispose)
    }
}

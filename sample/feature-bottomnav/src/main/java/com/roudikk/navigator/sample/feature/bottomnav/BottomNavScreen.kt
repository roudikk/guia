package com.roudikk.navigator.sample.feature.bottomnav

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.with
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisallowComposableCalls
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.roudikk.navigator.backstack.navhost.StackHistoryBackHandler
import com.roudikk.navigator.containers.NavContainer
import com.roudikk.navigator.core.NavigatorConfigBuilder
import com.roudikk.navigator.core.rememberNavigator
import com.roudikk.navigator.extensions.popToRoot
import com.roudikk.navigator.extensions.push
import com.roudikk.navigator.navhost.NavHost
import com.roudikk.navigator.navhost.StackEntry
import com.roudikk.navigator.navhost.StackKey
import com.roudikk.navigator.navhost.rememberNavHost
import com.roudikk.navigator.sample.feature.common.composables.SampleSurfaceContainer
import com.roudikk.navigator.sample.feature.common.deeplink.BottomTabDestination
import com.roudikk.navigator.sample.feature.common.deeplink.BottomTabDestination.CustomTab
import com.roudikk.navigator.sample.feature.common.deeplink.BottomTabDestination.DialogsTab
import com.roudikk.navigator.sample.feature.common.deeplink.BottomTabDestination.HomeTab
import com.roudikk.navigator.sample.feature.common.deeplink.BottomTabDestination.NestedTab
import com.roudikk.navigator.sample.feature.common.deeplink.DialogsDestination
import com.roudikk.navigator.sample.feature.common.deeplink.DialogsDestination.BlockingBottomSheet
import com.roudikk.navigator.sample.feature.common.deeplink.DialogsDestination.BlockingDialog
import com.roudikk.navigator.sample.feature.common.deeplink.DialogsDestination.Cancelable
import com.roudikk.navigator.sample.feature.common.deeplink.GlobalNavigator
import com.roudikk.navigator.sample.feature.common.deeplink.HomeDestination
import com.roudikk.navigator.sample.feature.common.navigation.LocalNavHostViewModelStoreOwner
import com.roudikk.navigator.sample.feature.common.theme.AppTheme
import com.roudikk.navigator.sample.feature.custom.api.CustomStackKey
import com.roudikk.navigator.sample.feature.custom.api.ViewPagerRootKey
import com.roudikk.navigator.sample.feature.details.api.DetailsKey
import com.roudikk.navigator.sample.feature.dialogs.api.BlockingBottomSheetKey
import com.roudikk.navigator.sample.feature.dialogs.api.BlockingDialogKey
import com.roudikk.navigator.sample.feature.dialogs.api.CancelableDialogKey
import com.roudikk.navigator.sample.feature.dialogs.api.DialogsKey
import com.roudikk.navigator.sample.feature.dialogs.api.DialogsStackKey
import com.roudikk.navigator.sample.feature.home.api.HomeKey
import com.roudikk.navigator.sample.feature.home.api.HomeStackKey
import com.roudikk.navigator.sample.feature.nested.api.NestedStackKey
import com.roudikk.navigator.sample.feature.nested.api.ParentNestedKey

@Composable
fun rememberBottomNavHost(
    homeNavigation: NavigatorConfigBuilder.() -> Unit,
    nestedNavigation: NavigatorConfigBuilder.() -> Unit,
    dialogsNavigation: NavigatorConfigBuilder.() -> Unit,
    customNavigation: NavigatorConfigBuilder.() -> Unit,
    initialize: @DisallowComposableCalls (NavHost) -> Unit,
): NavHost {
    val homeNavigator = rememberNavigator(
        initialKey = HomeKey(),
        builder = homeNavigation
    )

    val nestedNavigator = rememberNavigator(
        initialKey = ParentNestedKey(),
        builder = nestedNavigation
    )

    val dialogsNavigator = rememberNavigator(
        initialKey = DialogsKey(),
        builder = dialogsNavigation
    )

    val customNavigator = rememberNavigator(
        initialKey = ViewPagerRootKey(),
        builder = customNavigation
    )

    return rememberNavHost(
        initialKey = HomeStackKey,
        entries = setOf(
            StackEntry(HomeStackKey, homeNavigator),
            StackEntry(NestedStackKey, nestedNavigator),
            StackEntry(DialogsStackKey, dialogsNavigator),
            StackEntry(CustomStackKey, customNavigator),
        ),
        initialize = initialize,
    )
}

@Composable
fun BottomNavScreen(
    globalNavigator: GlobalNavigator = viewModel(LocalNavHostViewModelStoreOwner.current),
    homeNavigation: NavigatorConfigBuilder.() -> Unit,
    nestedNavigation: NavigatorConfigBuilder.() -> Unit,
    dialogsNavigation: NavigatorConfigBuilder.() -> Unit,
    customNavigation: NavigatorConfigBuilder.() -> Unit
) {
    val navHost = rememberBottomNavHost(
        homeNavigation = homeNavigation,
        nestedNavigation = nestedNavigation,
        dialogsNavigation = dialogsNavigation,
        customNavigation = customNavigation
    ) { it.deeplink(globalNavigator) }

    navHost.StackHistoryBackHandler()

    BottomNavContent(
        currentStackKey = navHost.currentEntry?.stackKey,
        popToRoot = { navHost.currentNavigator?.popToRoot() },
        setActive = navHost::setActive
    ) { padding ->
        NavHostContainer(
            navHost = navHost,
            padding = padding
        )
    }

    LaunchedEffect(globalNavigator.destinations) {
        navHost.deeplink(globalNavigator)
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun NavHostContainer(
    navHost: NavHost,
    padding: PaddingValues
) {
    val density = LocalDensity.current
    val imePadding = with(density) { WindowInsets.ime.getBottom(this).toDp() }
    val navBarsPadding = with(density) { WindowInsets.navigationBars.getBottom(this).toDp() }

    navHost.NavContainer(
        modifier = {
            Modifier
                .padding(
                    bottom = maxOf(
                        padding.calculateBottomPadding() + navBarsPadding,
                        imePadding
                    )
                )
        },
        bottomSheetScrimColor = {
            Color.Black.copy(alpha = 0.32F)
        },
        bottomSheetContainer = { _, content ->
            SampleSurfaceContainer(
                modifier = Modifier
                    .padding(16.dp)
                    .statusBarsPadding(),
                content = content
            )
        },
        dialogContainer = { _, content ->
            SampleSurfaceContainer(
                modifier = Modifier
                    .padding(16.dp)
                    .widthIn(max = 350.dp),
                content = content
            )
        },
        transitionSpec = {
            if (targetState?.stackKey is CustomStackKey) {
                slideInHorizontally { it } with slideOutHorizontally { -it }
            } else {
                if (initialState?.stackKey is CustomStackKey) {
                    slideInHorizontally { -it } with slideOutHorizontally { it }
                } else {
                    fadeIn() with fadeOut()
                }
            }
        }
    )
}

private fun NavHost.deeplink(globalNavigator: GlobalNavigator) {
    globalNavigator.destinations
        .filterIsInstance<BottomTabDestination>()
        .forEach { destination ->
            when (destination) {
                HomeTab -> setActive(HomeStackKey)
                NestedTab -> setActive(NestedStackKey)
                DialogsTab -> setActive(DialogsStackKey)
                CustomTab -> setActive(CustomStackKey)
            }
        }

    globalNavigator.destinations
        .filterIsInstance<DialogsDestination>()
        .forEach { destination ->
            when (destination) {
                BlockingBottomSheet -> navigator(DialogsStackKey).push(
                    navigationKey = BlockingBottomSheetKey()
                )

                BlockingDialog -> navigator(DialogsStackKey).push(
                    navigationKey = BlockingDialogKey(showNextButton = false)
                )

                Cancelable -> navigator(DialogsStackKey).push(
                    navigationKey = CancelableDialogKey(showNextButton = false)
                )
            }
        }

    globalNavigator.destinations
        .filterIsInstance<HomeDestination>()
        .forEach { destination ->
            when (destination) {
                is HomeDestination.Details -> navigator(HomeStackKey).push(
                    navigationKey = DetailsKey(item = destination.item)
                )
            }
        }

    globalNavigator.onBottomTabDestinationsHandled()
    globalNavigator.onHomeDestinationsHandled()
    globalNavigator.onDialogsDestinationsHandled()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BottomNavContent(
    currentStackKey: StackKey?,
    popToRoot: () -> Unit,
    setActive: (StackKey) -> Unit,
    content: @Composable (PaddingValues) -> Unit
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets(0.dp),
        bottomBar = {
            BottomNavigationBar(
                currentStackKey = currentStackKey,
                popToRoot = popToRoot,
                setActive = setActive
            )
        },
        content = content
    )
}

@Composable
private fun BottomNavigationBar(
    currentStackKey: StackKey?,
    popToRoot: () -> Unit,
    setActive: (StackKey) -> Unit
) {
    NavigationBar {
        BottomNavTab.values().forEach { tab ->
            NavigationBarItem(
                modifier = Modifier
                    .navigationBarsPadding()
                    .testTag(tab.tag),
                label = { Text(tab.label) },
                selected = currentStackKey == tab.stackKey,
                onClick = {
                    navigatorToStackOrRoot(
                        popToRoot = popToRoot,
                        setActive = setActive,
                        currentKey = currentStackKey,
                        newKey = tab.stackKey
                    )
                },
                icon = {
                    Icon(
                        imageVector = tab.icon,
                        contentDescription = tab.label
                    )
                }
            )
        }
    }
}

private fun navigatorToStackOrRoot(
    popToRoot: () -> Unit,
    setActive: (StackKey) -> Unit,
    currentKey: StackKey?,
    newKey: StackKey
) {
    if (currentKey == newKey) {
        popToRoot()
    } else {
        setActive(newKey)
    }
}

@Preview(
    device = Devices.PIXEL_3
)
@Preview(
    uiMode = UI_MODE_NIGHT_YES,
    device = Devices.PIXEL_3
)
@Composable
private fun BottomNavContentPreviewDark() = AppTheme {
    BottomNavContent(
        currentStackKey = null,
        popToRoot = {},
        setActive = {},
        content = {}
    )
}

package com.roudikk.navigator.sample.feature.bottomnav

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.with
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Grid4x4
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.StackedBarChart
import androidx.compose.material.icons.filled.Window
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
import com.roudikk.navigator.extensions.navigate
import com.roudikk.navigator.extensions.popToRoot
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
import com.roudikk.navigator.sample.feature.custom.api.ViewPagerRootKey
import com.roudikk.navigator.sample.feature.custom.api.ViewPagerStackKey
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
            StackEntry(ViewPagerStackKey, customNavigator),
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

    BottomNavContent(navHost)

    LaunchedEffect(globalNavigator.destinations) {
        navHost.deeplink(globalNavigator)
    }
}

private fun NavHost.deeplink(globalNavigator: GlobalNavigator) {
    globalNavigator.destinations
        .filterIsInstance<BottomTabDestination>()
        .forEach { destination ->
            when (destination) {
                HomeTab -> setActive(HomeStackKey)
                NestedTab -> setActive(NestedStackKey)
                DialogsTab -> setActive(DialogsStackKey)
                CustomTab -> setActive(ViewPagerStackKey)
            }
        }

    globalNavigator.destinations
        .filterIsInstance<DialogsDestination>()
        .forEach { destination ->
            when (destination) {
                BlockingBottomSheet -> navigator(DialogsStackKey).navigate(
                    navigationKey = BlockingBottomSheetKey()
                )

                BlockingDialog -> navigator(DialogsStackKey).navigate(
                    navigationKey = BlockingDialogKey(showNextButton = false)
                )

                Cancelable -> navigator(DialogsStackKey).navigate(
                    navigationKey = CancelableDialogKey(showNextButton = false)
                )
            }
        }

    globalNavigator.destinations
        .filterIsInstance<HomeDestination>()
        .forEach { destination ->
            when (destination) {
                is HomeDestination.Details -> navigator(HomeStackKey).navigate(
                    navigationKey = DetailsKey(item = destination.item)
                )
            }
        }

    globalNavigator.onBottomTabDestinationsHandled()
    globalNavigator.onHomeDestinationsHandled()
    globalNavigator.onDialogsDestinationsHandled()
}

@OptIn(
    ExperimentalAnimationApi::class,
    ExperimentalMaterial3Api::class
)
@Composable
private fun BottomNavContent(
    navHost: NavHost
) {
    val density = LocalDensity.current
    val imePadding = with(density) { WindowInsets.ime.getBottom(this).toDp() }
    val navBarsPadding = with(density) { WindowInsets.navigationBars.getBottom(this).toDp() }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets(0.dp),
        bottomBar = { BottomNavigation(navHost) }
    ) { padding ->
        navHost.NavContainer(
            modifier = {
                Modifier
                    .padding(bottom = maxOf(padding.calculateBottomPadding() + navBarsPadding, imePadding))
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
                    modifier = Modifier.padding(16.dp),
                    content = content
                )
            },
            transitionSpec = {
                if (targetState?.stackKey is ViewPagerStackKey) {
                    slideInHorizontally { it } with slideOutHorizontally { -it }
                } else {
                    if (initialState?.stackKey is ViewPagerStackKey) {
                        slideInHorizontally { -it } with slideOutHorizontally { it }
                    } else {
                        fadeIn() with fadeOut()
                    }
                }
            }
        )
    }
}

@Composable
private fun BottomNavigation(navHost: NavHost) {
    val currentStackKey = navHost.currentEntry?.stackKey

    NavigationBar {
        NavigationBarItem(
            modifier = Modifier
                .navigationBarsPadding()
                .testTag("tab_home"),
            label = { Text("Home") },
            selected = currentStackKey == HomeStackKey,
            onClick = {
                navigatorToStackOrRoot(
                    navHost = navHost,
                    currentKey = currentStackKey,
                    newKey = HomeStackKey
                )
            },
            icon = {
                Icon(
                    imageVector = Icons.Default.Home,
                    contentDescription = "Home"
                )
            }
        )

        NavigationBarItem(
            modifier = Modifier
                .navigationBarsPadding()
                .testTag("tab_nested"),
            label = { Text("Nested") },
            selected = currentStackKey == NestedStackKey,
            onClick = {
                navigatorToStackOrRoot(
                    navHost = navHost,
                    currentKey = currentStackKey,
                    newKey = NestedStackKey
                )
            },
            icon = {
                Icon(
                    imageVector = Icons.Default.StackedBarChart,
                    contentDescription = "Nested"
                )
            }
        )

        NavigationBarItem(
            modifier = Modifier
                .navigationBarsPadding()
                .testTag("tab_dialogs"),
            label = { Text("Dialogs") },
            selected = currentStackKey == DialogsStackKey,
            onClick = {
                navigatorToStackOrRoot(
                    navHost = navHost,
                    currentKey = currentStackKey,
                    newKey = DialogsStackKey
                )
            },
            icon = {
                Icon(
                    imageVector = Icons.Default.Window,
                    contentDescription = "Dialogs"
                )
            }
        )

        NavigationBarItem(
            modifier = Modifier
                .navigationBarsPadding()
                .testTag("tab_custom"),
            label = { Text("Custom") },
            selected = currentStackKey == ViewPagerStackKey,
            onClick = {
                navigatorToStackOrRoot(
                    navHost = navHost,
                    currentKey = currentStackKey,
                    newKey = ViewPagerStackKey
                )
            },
            icon = {
                Icon(
                    imageVector = Icons.Default.Grid4x4,
                    contentDescription = "Dialogs"
                )
            }
        )
    }
}

private fun navigatorToStackOrRoot(
    navHost: NavHost,
    currentKey: StackKey?,
    newKey: StackKey
) {
    if (currentKey == newKey) {
        navHost.currentNavigator?.popToRoot()
    } else {
        navHost.setActive(newKey)
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
    BottomNavScreen(
        globalNavigator = GlobalNavigator(),
        homeNavigation = {},
        nestedNavigation = {},
        dialogsNavigation = {},
        customNavigation = {},
    )
}

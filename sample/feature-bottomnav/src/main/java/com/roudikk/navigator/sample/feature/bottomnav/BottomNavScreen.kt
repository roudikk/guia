package com.roudikk.navigator.sample.feature.bottomnav

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.with
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountTree
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.roudikk.navigator.backstack.navhost.DefaultStackBackHandler
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
import com.roudikk.navigator.sample.feature.common.deeplink.BottomNavDestination.DialogsTab
import com.roudikk.navigator.sample.feature.common.deeplink.BottomNavDestination.HomeTab
import com.roudikk.navigator.sample.feature.common.deeplink.BottomNavDestination.NavigationTreeTab
import com.roudikk.navigator.sample.feature.common.deeplink.BottomNavDestination.NestedTab
import com.roudikk.navigator.sample.feature.common.deeplink.DeepLinkViewModel
import com.roudikk.navigator.sample.feature.common.deeplink.DialogsDestination.BlockingBottomSheet
import com.roudikk.navigator.sample.feature.common.deeplink.DialogsDestination.BlockingDialog
import com.roudikk.navigator.sample.feature.common.deeplink.DialogsDestination.Cancelable
import com.roudikk.navigator.sample.feature.common.deeplink.HomeDestination.Details
import com.roudikk.navigator.sample.feature.common.navigation.LocalNavHostViewModelStoreOwner
import com.roudikk.navigator.sample.feature.common.theme.AppTheme
import com.roudikk.navigator.sample.feature.details.api.DetailsKey
import com.roudikk.navigator.sample.feature.dialogs.api.BlockingBottomSheetKey
import com.roudikk.navigator.sample.feature.dialogs.api.BlockingDialogKey
import com.roudikk.navigator.sample.feature.dialogs.api.CancelableDialogKey
import com.roudikk.navigator.sample.feature.dialogs.api.DialogsKey
import com.roudikk.navigator.sample.feature.dialogs.api.DialogsStackKey
import com.roudikk.navigator.sample.feature.home.api.HomeKey
import com.roudikk.navigator.sample.feature.home.api.HomeStackKey
import com.roudikk.navigator.sample.feature.navtree.api.NavigationTreeKey
import com.roudikk.navigator.sample.feature.navtree.api.NavigationTreeStackKey
import com.roudikk.navigator.sample.feature.nested.api.NestedStackKey
import com.roudikk.navigator.sample.feature.nested.api.ParentNestedKey

@Composable
fun rememberBottomNavHost(
    homeNavigation: NavigatorConfigBuilder.() -> Unit,
    nestedNavigation: NavigatorConfigBuilder.() -> Unit,
    dialogsNavigation: NavigatorConfigBuilder.() -> Unit,
    navigationTreeNavigation: NavigatorConfigBuilder.() -> Unit,
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

    val navigationTreeNavigator = rememberNavigator(
        initialKey = NavigationTreeKey(),
        builder = navigationTreeNavigation
    )

    return rememberNavHost(
        initialKey = HomeStackKey,
        entries = setOf(
            StackEntry(HomeStackKey, homeNavigator),
            StackEntry(NestedStackKey, nestedNavigator),
            StackEntry(DialogsStackKey, dialogsNavigator),
            StackEntry(NavigationTreeStackKey, navigationTreeNavigator)
        ),
        initialize = initialize,
    )
}

@Composable
fun BottomNavScreen(
    homeNavigation: NavigatorConfigBuilder.() -> Unit,
    nestedNavigation: NavigatorConfigBuilder.() -> Unit,
    dialogsNavigation: NavigatorConfigBuilder.() -> Unit,
    navigationTreeNavigation: NavigatorConfigBuilder.() -> Unit
) {
    val deepLinkViewModel = viewModel<DeepLinkViewModel>(LocalNavHostViewModelStoreOwner.current)

    val navHost = rememberBottomNavHost(
        homeNavigation = homeNavigation,
        nestedNavigation = nestedNavigation,
        dialogsNavigation = dialogsNavigation,
        navigationTreeNavigation = navigationTreeNavigation
    ) { it.deeplink(deepLinkViewModel) }

    BottomNavContent(navHost)

    navHost.DefaultStackBackHandler(HomeStackKey)

    LaunchedEffect(deepLinkViewModel.destinations) {
        navHost.deeplink(deepLinkViewModel)
    }
}

private fun NavHost.deeplink(deepLinkViewModel: DeepLinkViewModel) {
    deepLinkViewModel.destinations
        .forEach { destination ->
            when (destination) {
                // Tab destinations
                HomeTab -> setActive(HomeStackKey)
                NestedTab -> setActive(NestedStackKey)
                DialogsTab -> setActive(DialogsStackKey)
                NavigationTreeTab -> setActive(NavigationTreeStackKey)

                // Dialog destinations
                BlockingBottomSheet -> currentNavigator?.navigate(BlockingBottomSheetKey())
                BlockingDialog -> currentNavigator?.navigate(BlockingDialogKey(false))
                Cancelable -> currentNavigator?.navigate(CancelableDialogKey(false))

                // Home destinations
                is Details -> currentNavigator?.navigate(DetailsKey(destination.item))

                // Ignore other destinations
                else -> Unit
            }
        }
    deepLinkViewModel.onBottomNavDestinationsHandled()
}

@OptIn(
    ExperimentalAnimationApi::class,
    ExperimentalMaterial3Api::class
)
@Composable
private fun BottomNavContent(
    navHost: NavHost
) {
    Scaffold(
        modifier = Modifier
            .fillMaxSize(),
        bottomBar = { BottomNavigation(navHost) }
    ) { padding ->
        navHost.NavContainer(
            modifier = {
                Modifier
                    .padding(bottom = 80.dp)
            },
            bottomSheetContainer = { _, content ->
                SampleSurfaceContainer(
                    modifier = Modifier
                        .padding(16.dp)
                        .navigationBarsPadding()
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
                if (targetState?.stackKey is NavigationTreeStackKey) {
                    slideInHorizontally { it } with slideOutHorizontally { -it }
                } else {
                    if (initialState?.stackKey is NavigationTreeStackKey) {
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
                    navHost,
                    currentStackKey,
                    HomeStackKey
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
                    navHost,
                    currentStackKey,
                    NestedStackKey
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
                    navHost,
                    currentStackKey,
                    DialogsStackKey
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
                .testTag("tab_nav_tree"),
            label = { Text("Nav Tree") },
            selected = currentStackKey == NavigationTreeStackKey,
            onClick = {
                navigatorToStackOrRoot(
                    navHost,
                    currentStackKey,
                    NavigationTreeStackKey
                )
            },
            icon = {
                Icon(
                    imageVector = Icons.Default.AccountTree,
                    contentDescription = "Nav Tree"
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
        homeNavigation = {},
        nestedNavigation = {},
        dialogsNavigation = {},
        navigationTreeNavigation = {}
    )
}

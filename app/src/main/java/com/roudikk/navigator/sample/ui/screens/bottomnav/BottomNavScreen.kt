package com.roudikk.navigator.sample.ui.screens.bottomnav

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.with
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountTree
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.StackedBarChart
import androidx.compose.material.icons.filled.Window
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisallowComposableCalls
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.roudikk.navigator.NavigatorBuilderScope
import com.roudikk.navigator.backstack.DefaultStackBackHandler
import com.roudikk.navigator.core.NavigationKey
import com.roudikk.navigator.extensions.navigate
import com.roudikk.navigator.extensions.popToRoot
import com.roudikk.navigator.navhost.NavContainer
import com.roudikk.navigator.navhost.NavHost
import com.roudikk.navigator.navhost.StackEntry
import com.roudikk.navigator.navhost.StackKey
import com.roudikk.navigator.navhost.rememberNavHost
import com.roudikk.navigator.rememberNavigator
import com.roudikk.navigator.sample.BottomNavDestination.DialogsTab
import com.roudikk.navigator.sample.BottomNavDestination.HomeTab
import com.roudikk.navigator.sample.BottomNavDestination.NavigationTreeTab
import com.roudikk.navigator.sample.BottomNavDestination.NestedTab
import com.roudikk.navigator.sample.DeepLinkViewModel
import com.roudikk.navigator.sample.DialogsDestination.BlockingBottomSheet
import com.roudikk.navigator.sample.DialogsDestination.BlockingDialog
import com.roudikk.navigator.sample.DialogsDestination.Cancelable
import com.roudikk.navigator.sample.HomeDestination.Details
import com.roudikk.navigator.sample.navigation.LocalNavHostViewModelStoreOwner
import com.roudikk.navigator.sample.navigation.MaterialSharedAxisTransitionX
import com.roudikk.navigator.sample.ui.composables.sampleBottomSheetOptions
import com.roudikk.navigator.sample.ui.screens.details.DetailsKey
import com.roudikk.navigator.sample.ui.screens.details.detailsNavigation
import com.roudikk.navigator.sample.ui.screens.dialogs.BlockingBottomSheetKey
import com.roudikk.navigator.sample.ui.screens.dialogs.BlockingDialogKey
import com.roudikk.navigator.sample.ui.screens.dialogs.CancelableDialogKey
import com.roudikk.navigator.sample.ui.screens.dialogs.DialogsKey
import com.roudikk.navigator.sample.ui.screens.dialogs.DialogsStackKey
import com.roudikk.navigator.sample.ui.screens.dialogs.blockingDialogNavigation
import com.roudikk.navigator.sample.ui.screens.dialogs.cancelableDialogNavigation
import com.roudikk.navigator.sample.ui.screens.dialogs.dialogsNavigation
import com.roudikk.navigator.sample.ui.screens.home.HomeKey
import com.roudikk.navigator.sample.ui.screens.home.HomeStackKey
import com.roudikk.navigator.sample.ui.screens.home.homeNavigation
import com.roudikk.navigator.sample.ui.screens.navigationtree.NavigationTreeKey
import com.roudikk.navigator.sample.ui.screens.navigationtree.NavigationTreeStackKey
import com.roudikk.navigator.sample.ui.screens.navigationtree.navigationTreeNavigation
import com.roudikk.navigator.sample.ui.screens.nested.NestedStackKey
import com.roudikk.navigator.sample.ui.screens.nested.ParentNestedKey
import com.roudikk.navigator.sample.ui.screens.nested.nestedNavigation
import com.roudikk.navigator.sample.ui.screens.nested.parentNestedNavigation
import com.roudikk.navigator.sample.ui.theme.AppTheme
import kotlinx.parcelize.Parcelize

@Parcelize
class BottomNavKey : NavigationKey

fun NavigatorBuilderScope.bottomTabNavigation() {
    screen<BottomNavKey> { BottomNavScreen() }
}

@Composable
fun rememberBottomNavHost(
    initialize: @DisallowComposableCalls (NavHost) -> Unit = {}
): NavHost {
    val configuration = LocalConfiguration.current

    val homeNavigator = rememberNavigator(initialKey = HomeKey()) {
        defaultTransition { -> MaterialSharedAxisTransitionX }
        homeNavigation()
        detailsNavigation(configuration.screenWidthDp)
    }

    val nestedNavigator = rememberNavigator(initialKey = ParentNestedKey()) {
        parentNestedNavigation()
        nestedNavigation()
    }

    val dialogsNavigator = rememberNavigator(initialKey = DialogsKey()) {
        dialogsNavigation()
        blockingDialogNavigation()
        cancelableDialogNavigation()
    }

    val navigationTreeNavigator = rememberNavigator(initialKey = NavigationTreeKey()) {
        navigationTreeNavigation()
    }

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
fun BottomNavScreen() {
    val deepLinkViewModel = viewModel<DeepLinkViewModel>(LocalNavHostViewModelStoreOwner.current)

    val navHost = rememberBottomNavHost { it.deeplink(deepLinkViewModel) }

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
            modifier = { Modifier.padding(bottom = 80.dp) },
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
            },
            bottomSheetSetup = {
                sampleBottomSheetOptions(Modifier.padding(padding))
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
    BottomNavScreen()
}

package com.roudikk.guia.sample.feature.bottomnav

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.widthIn
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
import com.roudikk.guia.backstack.navhost.StackHistoryBackHandler
import com.roudikk.guia.containers.NavContainer
import com.roudikk.guia.core.NavigatorConfigBuilder
import com.roudikk.guia.core.rememberNavigator
import com.roudikk.guia.extensions.popToRoot
import com.roudikk.guia.extensions.push
import com.roudikk.guia.navhost.NavHost
import com.roudikk.guia.navhost.StackKey
import com.roudikk.guia.navhost.rememberNavHost
import com.roudikk.guia.navhost.to
import com.roudikk.guia.sample.feature.common.composables.SampleSurfaceContainer
import com.roudikk.guia.sample.feature.common.deeplink.BottomTabDestination
import com.roudikk.guia.sample.feature.common.deeplink.BottomTabDestination.CustomTab
import com.roudikk.guia.sample.feature.common.deeplink.BottomTabDestination.DialogsTab
import com.roudikk.guia.sample.feature.common.deeplink.BottomTabDestination.HomeTab
import com.roudikk.guia.sample.feature.common.deeplink.BottomTabDestination.NestedTab
import com.roudikk.guia.sample.feature.common.deeplink.DialogsDestination
import com.roudikk.guia.sample.feature.common.deeplink.DialogsDestination.BlockingBottomSheet
import com.roudikk.guia.sample.feature.common.deeplink.DialogsDestination.BlockingDialog
import com.roudikk.guia.sample.feature.common.deeplink.DialogsDestination.Cancelable
import com.roudikk.guia.sample.feature.common.deeplink.GlobalNavigator
import com.roudikk.guia.sample.feature.common.deeplink.HomeDestination
import com.roudikk.guia.sample.feature.common.navigation.LocalNavHostViewModelStoreOwner
import com.roudikk.guia.sample.feature.common.theme.AppTheme
import com.roudikk.guia.sample.feature.custom.navigation.CustomStackKey
import com.roudikk.guia.sample.feature.custom.navigation.ViewPagerRootKey
import com.roudikk.guia.sample.feature.details.navigation.DetailsKey
import com.roudikk.guia.sample.feature.dialogs.navigation.BlockingBottomSheetKey
import com.roudikk.guia.sample.feature.dialogs.navigation.BlockingDialogKey
import com.roudikk.guia.sample.feature.dialogs.navigation.CancelableDialogKey
import com.roudikk.guia.sample.feature.dialogs.navigation.DialogsKey
import com.roudikk.guia.sample.feature.dialogs.navigation.DialogsStackKey
import com.roudikk.guia.sample.feature.home.navigation.HomeKey
import com.roudikk.guia.sample.feature.home.navigation.HomeStackKey
import com.roudikk.guia.sample.feature.nested.navigation.NestedStackKey
import com.roudikk.guia.sample.feature.nested.navigation.ParentNestedKey

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
            HomeStackKey to homeNavigator,
            NestedStackKey to nestedNavigator,
            DialogsStackKey to dialogsNavigator,
            CustomStackKey to customNavigator,
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

@Composable
private fun NavHostContainer(
    navHost: NavHost,
    padding: PaddingValues
) {
    val density = LocalDensity.current
    val imePadding = with(density) { WindowInsets.ime.getBottom(this).toDp() }

    navHost.NavContainer(
        modifier = {
            Modifier
                .padding(
                    bottom = maxOf(
                        padding.calculateBottomPadding(),
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
                slideInHorizontally { it } togetherWith slideOutHorizontally { -it }
            } else {
                if (initialState?.stackKey is CustomStackKey) {
                    slideInHorizontally { -it } togetherWith slideOutHorizontally { it }
                } else {
                    fadeIn() togetherWith fadeOut()
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

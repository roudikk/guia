package com.roudikk.navigator.sample.ui.screens.bottomnav

import android.content.res.Configuration.UI_MODE_NIGHT_YES
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.roudikk.navigator.NavContainer
import com.roudikk.navigator.NavHost
import com.roudikk.navigator.NavigationKey
import com.roudikk.navigator.NavigatorRulesScope
import com.roudikk.navigator.core.StackKey
import com.roudikk.navigator.popToRoot
import com.roudikk.navigator.rememberNavHost
import com.roudikk.navigator.rememberNavigator
import com.roudikk.navigator.sample.navigation.SampleStackKey
import com.roudikk.navigator.sample.ui.composables.sampleBottomSheetOptions
import com.roudikk.navigator.sample.ui.screens.details.detailsNavigation
import com.roudikk.navigator.sample.ui.screens.dialogs.DialogsKey
import com.roudikk.navigator.sample.ui.screens.dialogs.blockingBottomSheetNavigation
import com.roudikk.navigator.sample.ui.screens.dialogs.blockingDialogNavigation
import com.roudikk.navigator.sample.ui.screens.dialogs.cancelableDialogNavigation
import com.roudikk.navigator.sample.ui.screens.dialogs.dialogsNavigation
import com.roudikk.navigator.sample.ui.screens.home.HomeKey
import com.roudikk.navigator.sample.ui.screens.home.homeNavigation
import com.roudikk.navigator.sample.ui.screens.navigationtree.NavigationTreeKey
import com.roudikk.navigator.sample.ui.screens.navigationtree.navigationTreeNavigation
import com.roudikk.navigator.sample.ui.screens.nested.ParentNestedKey
import com.roudikk.navigator.sample.ui.screens.nested.nestedNavigation
import com.roudikk.navigator.sample.ui.screens.nested.parentNestedNavigation
import com.roudikk.navigator.sample.ui.theme.AppTheme
import kotlinx.parcelize.Parcelize

@Parcelize
class BottomTabKey : NavigationKey

fun NavigatorRulesScope.bottomTabNavigation() {
    screen<BottomTabKey> { BottomNavScreen() }
}

@Composable
fun BottomNavScreen() {
    val configuration = LocalConfiguration.current
    val homeNavigator = rememberNavigator(initialKey = HomeKey()) {
        homeNavigation()
        detailsNavigation(configuration.screenWidthDp)
    }

    val nestedNavigator = rememberNavigator(initialKey = ParentNestedKey()) {
        parentNestedNavigation()
        nestedNavigation()
    }

    val dialogsNavigator = rememberNavigator(initialKey = DialogsKey()) {
        dialogsNavigation()
        blockingBottomSheetNavigation()
        blockingDialogNavigation()
        cancelableDialogNavigation()
    }

    val navigationTreeNavigator = rememberNavigator(initialKey = NavigationTreeKey()) {
        navigationTreeNavigation()
    }

    val navHost = rememberNavHost(
        initialKey = SampleStackKey.Home,
        navigatorKeyMap = hashMapOf(
            SampleStackKey.Home to homeNavigator,
            SampleStackKey.Nested to nestedNavigator,
            SampleStackKey.Dialogs to dialogsNavigator,
            SampleStackKey.StackTree to navigationTreeNavigator
        )
    )

    BottomNavContent(navHost)
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
            modifier = Modifier.padding(bottom = 80.dp),
            bottomSheetOptions = sampleBottomSheetOptions(
                Modifier.padding(padding)
            )
        )
    }
}

@Composable
private fun BottomNavigation(navHost: NavHost) {
    val currentStackKey = navHost.activeKey

    NavigationBar {
        NavigationBarItem(
            modifier = Modifier
                .navigationBarsPadding()
                .testTag("tab_home"),
            label = { Text("Home") },
            selected = currentStackKey == SampleStackKey.Home,
            onClick = {
                navigatorToStackOrRoot(
                    navHost,
                    currentStackKey,
                    SampleStackKey.Home
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
            selected = currentStackKey == SampleStackKey.Nested,
            onClick = {
                navigatorToStackOrRoot(
                    navHost,
                    currentStackKey,
                    SampleStackKey.Nested
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
            selected = currentStackKey == SampleStackKey.Dialogs,
            onClick = {
                navigatorToStackOrRoot(
                    navHost,
                    currentStackKey,
                    SampleStackKey.Dialogs
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
            selected = currentStackKey == SampleStackKey.StackTree,
            onClick = {
                navigatorToStackOrRoot(
                    navHost,
                    currentStackKey,
                    SampleStackKey.StackTree
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
    currentKey: StackKey,
    newKey: StackKey
) {
    val navigator = navHost.activeNavigator
    if (currentKey == newKey) {
        navigator.popToRoot()
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
//    BottomNavContent(rememberNavigator())
}

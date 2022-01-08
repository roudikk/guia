package com.roudikk.composenavigator.ui.screens.bottomnav

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.UiMode
import androidx.compose.ui.unit.dp
import com.google.accompanist.insets.navigationBarsHeight
import com.google.accompanist.insets.navigationBarsPadding
import com.roudikk.compose_navigator.*
import com.roudikk.composenavigator.AppNavigationKey
import com.roudikk.composenavigator.AppPreview
import com.roudikk.composenavigator.ui.composables.defaultBottomSheetSetup
import com.roudikk.composenavigator.ui.screens.dialogs.DialogsScreen
import com.roudikk.composenavigator.ui.screens.home.HomeScreen
import com.roudikk.composenavigator.ui.screens.navigation_tree.NavigationTreeScreen
import com.roudikk.composenavigator.ui.screens.nested.ParentNestedScreen
import kotlinx.parcelize.Parcelize

@Parcelize
class BottomNavScreen : Screen {

    @Composable
    override fun Content(animatedVisibilityScope: AnimatedVisibilityScope) {
        val stackEntries = listOf(
            NavigationConfig.MultiStack.NavigationStackEntry(
                key = AppNavigationKey.Home,
                initialNavigationNode = HomeScreen()
            ),
            NavigationConfig.MultiStack.NavigationStackEntry(
                key = AppNavigationKey.Nested,
                initialNavigationNode = ParentNestedScreen()
            ),
            NavigationConfig.MultiStack.NavigationStackEntry(
                key = AppNavigationKey.Dialogs,
                initialNavigationNode = DialogsScreen()
            ),
            NavigationConfig.MultiStack.NavigationStackEntry(
                key = AppNavigationKey.NavigationTree,
                initialNavigationNode = NavigationTreeScreen()
            )
        )

        NavHost(
            key = "Home Navigation",
            navigationConfig = NavigationConfig.MultiStack(
                entries = stackEntries,
                initialStackKey = stackEntries[0].key,
                backStackStrategy = BackStackStrategy.BackToInitialStack()
            ),
        ) {
            BottomNavContent()
        }
    }
}

@Composable
private fun BottomNavContent() {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            BottomNavigation()
        }
    ) { paddingValues ->
        NavContainer(
            modifier = Modifier.padding(paddingValues),
            bottomSheetSetup = defaultBottomSheetSetup(
                Modifier.padding(paddingValues)
            )
        )
    }
}

@Composable
private fun BottomNavigation() {
    val navigator = findNavigator()
    val currentStackKey by navigator.currentKeyFlow.collectAsState()

    NavigationBar(
        modifier = Modifier
            .navigationBarsHeight(80.dp)
    ) {
        NavigationBarItem(
            modifier = Modifier.navigationBarsPadding(),
            label = { Text("Home") },
            selected = currentStackKey == AppNavigationKey.Home,
            onClick = {
                navigatorToStackOrRoot(
                    navigator,
                    currentStackKey,
                    AppNavigationKey.Home
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
            modifier = Modifier.navigationBarsPadding(),
            label = { Text("Nested") },
            selected = currentStackKey == AppNavigationKey.Nested,
            onClick = {
                navigatorToStackOrRoot(
                    navigator,
                    currentStackKey,
                    AppNavigationKey.Nested
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
            modifier = Modifier.navigationBarsPadding(),
            label = { Text("Dialogs") },
            selected = currentStackKey == AppNavigationKey.Dialogs,
            onClick = {
                navigatorToStackOrRoot(
                    navigator,
                    currentStackKey,
                    AppNavigationKey.Dialogs
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
            modifier = Modifier.navigationBarsPadding(),
            label = { Text("Nav Tree") },
            selected = currentStackKey == AppNavigationKey.NavigationTree,
            onClick = {
                navigatorToStackOrRoot(
                    navigator,
                    currentStackKey,
                    AppNavigationKey.NavigationTree
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
    navigator: Navigator,
    currentKey: NavigationKey,
    newKey: NavigationKey
) {
    if (currentKey == newKey) {
        navigator.popToRoot()
    } else {
        navigator.navigateToStack(newKey)
    }
}

@Preview(
    device = Devices.PIXEL_3
)
@Composable
private fun BottomNavContentPreview() = AppPreview {
    BottomNavContent()
}

@Preview(
    uiMode = UI_MODE_NIGHT_YES,
    device = Devices.PIXEL_3
)
@Composable
private fun BottomNavContentPreviewDark() = AppPreview {
    BottomNavContent()
}
package com.roudikk.navigator.sample.ui.screens.bottomnav

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountTree
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.StackedBarChart
import androidx.compose.material.icons.filled.Window
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.insets.navigationBarsHeight
import com.google.accompanist.insets.navigationBarsPadding
import com.roudikk.navigator.Navigator
import com.roudikk.navigator.compose.NavContainer
import com.roudikk.navigator.core.Screen
import com.roudikk.navigator.core.StackKey
import com.roudikk.navigator.rememberNavigator
import com.roudikk.navigator.sample.DeepLinkViewModel
import com.roudikk.navigator.sample.TabDestination
import com.roudikk.navigator.sample.navigation.LocalNavHostViewModelStoreOwner
import com.roudikk.navigator.sample.navigation.SampleNavConfig
import com.roudikk.navigator.sample.navigation.SampleStackKey
import com.roudikk.navigator.sample.ui.composables.sampleBottomSheetOptions
import com.roudikk.navigator.sample.ui.screens.details.DetailsScreen
import com.roudikk.navigator.sample.ui.theme.AppTheme
import kotlinx.parcelize.Parcelize

@Parcelize
class BottomNavScreen : Screen {

    @Composable
    override fun Content() {
        val mainViewModel = viewModel<DeepLinkViewModel>(
            viewModelStoreOwner = LocalNavHostViewModelStoreOwner.current
        )
        val bottomTabNavigator = rememberNavigator(SampleNavConfig.BottomTab) { navigator ->
            navigator.deeplink(mainViewModel.tabDestinations)
        }

        LaunchedEffect(Unit) {
            mainViewModel.tabDestinationsFlow.collect { destinations ->
                bottomTabNavigator.deeplink(destinations)
            }
        }

        BottomNavContent(bottomTabNavigator)
    }

    private fun Navigator.deeplink(destinations: List<TabDestination>) {
        destinations.forEach { destination ->
            when (destination) {
                is TabDestination.Details -> navigate(DetailsScreen(destination.item))
                TabDestination.DialogsTab -> navigateToStack(SampleStackKey.Dialogs)
                TabDestination.HomeTab -> navigateToStack(SampleStackKey.Home)
                TabDestination.NestedTab -> navigateToStack(SampleStackKey.Nested)
                TabDestination.StackTreeTab -> navigateToStack(SampleStackKey.StackTree)
            }
        }
    }
}

@Composable
private fun BottomNavContent(
    bottomTabNavigator: Navigator
) {

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            BottomNavigation(bottomTabNavigator)
        }
    ) { paddingValues ->

        NavContainer(
            navigator = bottomTabNavigator,
            modifier = Modifier.padding(paddingValues),
            bottomSheetOptions = sampleBottomSheetOptions(
                Modifier.padding(paddingValues)
            )
        )
    }
}

@Composable
private fun BottomNavigation(navigator: Navigator) {
    val currentStackKey by navigator.currentStackKeyFlow.collectAsState()

    NavigationBar(
        modifier = Modifier
            .navigationBarsHeight(80.dp)
    ) {
        NavigationBarItem(
            modifier = Modifier
                .navigationBarsPadding()
                .testTag("tab_home"),
            label = { Text("Home") },
            selected = currentStackKey == SampleStackKey.Home,
            onClick = {
                navigatorToStackOrRoot(
                    navigator,
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
                    navigator,
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
                    navigator,
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
                    navigator,
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
    navigator: Navigator,
    currentKey: StackKey,
    newKey: StackKey
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
@Preview(
    uiMode = UI_MODE_NIGHT_YES,
    device = Devices.PIXEL_3
)
@Composable
private fun BottomNavContentPreviewDark() = AppTheme {
    BottomNavContent(rememberNavigator())
}

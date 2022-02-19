package com.roudikk.navigator.sample

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.graphics.Color
import androidx.core.view.WindowCompat
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.roudikk.navigator.Navigator
import com.roudikk.navigator.compose.NavContainer
import com.roudikk.navigator.core.NavigationNode
import com.roudikk.navigator.rememberNavigator
import com.roudikk.navigator.sample.navigation.LocalDefaultNavigator
import com.roudikk.navigator.sample.navigation.LocalNavHostViewModelStoreOwner
import com.roudikk.navigator.sample.navigation.SampleNavConfig
import com.roudikk.navigator.sample.ui.composables.sampleBottomSheetOptions
import com.roudikk.navigator.sample.ui.screens.bottomnav.BottomNavScreen
import com.roudikk.navigator.sample.ui.screens.settings.SettingsScreen
import com.roudikk.navigator.sample.ui.theme.AppTheme

class MainActivity : ComponentActivity() {

    private val viewModel: DeepLinkViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        viewModel.onCreate(intent.dataString)

        setContent {
            val systemUiController = rememberSystemUiController()

            systemUiController.setNavigationBarColor(
                color = Color.Transparent,
                darkIcons = !isSystemInDarkTheme(),
                navigationBarContrastEnforced = false
            )

            systemUiController.setStatusBarColor(
                color = Color.Transparent,
                darkIcons = !isSystemInDarkTheme(),
            )

            AppTheme {
                val defaultNavigator = rememberNavigator(SampleNavConfig.Default) { navigator ->
                    navigator.deeplink(viewModel.mainDestinations)
                }

                CompositionLocalProvider(
                    LocalDefaultNavigator provides defaultNavigator,
                    LocalNavHostViewModelStoreOwner provides requireNotNull(LocalViewModelStoreOwner.current)
                ) {
                    NavContainer(
                        navigator = defaultNavigator,
                        bottomSheetOptions = sampleBottomSheetOptions()
                    )
                }

                LaunchedEffect(Unit) {
                    viewModel.mainDestinationsFlow.collect {
                        defaultNavigator.deeplink(it)
                    }
                }
            }
        }
    }

    private fun Navigator.deeplink(destinations: List<MainDestination>) {
        destinations.forEach { destination ->
            when (destination) {
                MainDestination.BottomNav -> {
                    if (!any { it.key == NavigationNode.key<BottomNavScreen>() }) {
                        navigate(BottomNavScreen())
                    } else {
                        popTo<BottomNavScreen>()
                    }
                }
                MainDestination.Settings -> navigate(SettingsScreen())
            }
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        viewModel.onNewIntent(intent?.dataString)
    }
}

package com.roudikk.navigator.sample

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.roudikk.navigator.containers.NavContainer
import com.roudikk.navigator.core.Navigator
import com.roudikk.navigator.core.NavigatorConfigBuilder
import com.roudikk.navigator.core.rememberNavigator
import com.roudikk.navigator.extensions.popTo
import com.roudikk.navigator.extensions.push
import com.roudikk.navigator.extensions.setRoot
import com.roudikk.navigator.sample.feature.bottomnav.api.BottomNavKey
import com.roudikk.navigator.sample.feature.bottomnav.bottomNavNavigation
import com.roudikk.navigator.sample.feature.common.deeplink.GlobalNavigator
import com.roudikk.navigator.sample.feature.common.deeplink.MainDestination
import com.roudikk.navigator.sample.feature.common.navigation.LocalNavHostViewModelStoreOwner
import com.roudikk.navigator.sample.feature.common.navigation.LocalRootNavigator
import com.roudikk.navigator.sample.feature.common.navigation.MaterialSharedAxisTransitionXY
import com.roudikk.navigator.sample.feature.common.theme.AppTheme
import com.roudikk.navigator.sample.feature.custom.viewpager.viewPagerNavigation
import com.roudikk.navigator.sample.feature.details.detailsNavigation
import com.roudikk.navigator.sample.feature.dialogs.dialogsNavigation
import com.roudikk.navigator.sample.feature.home.homeNavigation
import com.roudikk.navigator.sample.feature.nested.nestedNavigation
import com.roudikk.navigator.sample.feature.settings.api.SettingsKey
import com.roudikk.navigator.sample.feature.settings.settingsNavigation
import com.roudikk.navigator.sample.feature.welcome.api.WelcomeKey
import com.roudikk.navigator.sample.feature.welcome.welcomeNavigation

class MainActivity : ComponentActivity() {

    private val globalNavigator: GlobalNavigator by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        globalNavigator.onDeeplinkData(intent.dataString)

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
                val configuration = LocalConfiguration.current
                val rootNavigator = rememberNavigator(
                    initialKey = WelcomeKey(),
                    initialize = { it.deeplink(globalNavigator) }
                ) { rootNavigation(configuration.screenWidthDp) }

                CompositionLocalProvider(
                    LocalRootNavigator provides rootNavigator,
                    LocalNavHostViewModelStoreOwner provides requireNotNull(LocalViewModelStoreOwner.current)
                ) {
                    rootNavigator.NavContainer(
                        bottomSheetContainer = { content ->
                            Surface(tonalElevation = 4.dp) { content() }
                        },
                        bottomSheetScrimColor = Color.Black.copy(alpha = 0.32F),
                    )
                }

                LaunchedEffect(globalNavigator.destinations) {
                    rootNavigator.deeplink(globalNavigator)
                }
            }
        }
    }

    private fun NavigatorConfigBuilder.rootNavigation(
        screenWidth: Int
    ) {
        welcomeNavigation()
        settingsNavigation()
        dialogsNavigation()
        bottomNavNavigation(
            homeNavigation = {
                homeNavigation()
                detailsNavigation(screenWidth)
                settingsNavigation()
            },
            nestedNavigation = { nestedNavigation() },
            dialogsNavigation = { dialogsNavigation() },
            customNavigation = { viewPagerNavigation() }
        )
        defaultTransition { -> MaterialSharedAxisTransitionXY }
    }

    private fun Navigator.deeplink(globalNavigator: GlobalNavigator) {
        globalNavigator.destinations
            .filterIsInstance<MainDestination>()
            .forEach { destination ->
                when (destination) {
                    MainDestination.BottomNav -> {
                        if (!popTo<BottomNavKey>()) {
                            setRoot(BottomNavKey())
                        }
                    }

                    MainDestination.Settings -> push(SettingsKey())
                }
            }
        globalNavigator.onMainDestinationsHandled()
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        globalNavigator.onDeeplinkData(intent?.dataString)
    }
}

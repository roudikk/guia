package com.roudikk.guia.sample

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material3.Surface
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.roudikk.guia.containers.NavContainer
import com.roudikk.guia.core.Navigator
import com.roudikk.guia.core.NavigatorConfigBuilder
import com.roudikk.guia.core.rememberNavigator
import com.roudikk.guia.extensions.popTo
import com.roudikk.guia.extensions.push
import com.roudikk.guia.extensions.setRoot
import com.roudikk.guia.sample.feature.bottomnav.bottomNavNavigation
import com.roudikk.guia.sample.feature.bottomnav.navigation.BottomNavKey
import com.roudikk.guia.sample.feature.common.deeplink.GlobalNavigator
import com.roudikk.guia.sample.feature.common.deeplink.MainDestination
import com.roudikk.guia.sample.feature.common.navigation.LocalNavHostViewModelStoreOwner
import com.roudikk.guia.sample.feature.common.navigation.LocalRootNavigator
import com.roudikk.guia.sample.feature.common.navigation.MaterialSharedAxisTransitionXY
import com.roudikk.guia.sample.feature.common.theme.AppTheme
import com.roudikk.guia.sample.feature.custom.viewpager.viewPagerNavigation
import com.roudikk.guia.sample.feature.details.detailsNavigation
import com.roudikk.guia.sample.feature.dialogs.dialogsNavigation
import com.roudikk.guia.sample.feature.home.homeNavigation
import com.roudikk.guia.sample.feature.nested.nestedNavigation
import com.roudikk.guia.sample.feature.settings.navigation.SettingsKey
import com.roudikk.guia.sample.feature.settings.settingsNavigation
import com.roudikk.guia.sample.feature.welcome.navigation.WelcomeKey
import com.roudikk.guia.sample.feature.welcome.welcomeNavigation

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
                            Surface(
                                modifier = Modifier.navigationBarsPadding(),
                                tonalElevation = 4.dp,
                                content = content
                            )
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

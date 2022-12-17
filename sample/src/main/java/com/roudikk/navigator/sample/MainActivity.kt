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
import com.roudikk.navigator.compose.NavContainer
import com.roudikk.navigator.rememberNavigator
import com.roudikk.navigator.Navigator
import com.roudikk.navigator.NavigatorBuilderScope
import com.roudikk.navigator.extensions.navigate
import com.roudikk.navigator.extensions.popTo
import com.roudikk.navigator.extensions.setRoot
import com.roudikk.navigator.sample.navigation.LocalNavHostViewModelStoreOwner
import com.roudikk.navigator.sample.navigation.LocalRootNavigator
import com.roudikk.navigator.sample.navigation.MaterialSharedAxisTransitionXY
import com.roudikk.navigator.sample.ui.composables.sampleBottomSheetOptions
import com.roudikk.navigator.sample.ui.screens.bottomnav.BottomNavKey
import com.roudikk.navigator.sample.ui.screens.bottomnav.bottomTabNavigation
import com.roudikk.navigator.sample.ui.screens.dialogs.blockingBottomSheetNavigation
import com.roudikk.navigator.sample.ui.screens.settings.SettingsKey
import com.roudikk.navigator.sample.ui.screens.settings.settingsNavigation
import com.roudikk.navigator.sample.ui.screens.welcome.WelcomeKey
import com.roudikk.navigator.sample.ui.screens.welcome.welcomeNavigation
import com.roudikk.navigator.sample.ui.theme.AppTheme

class MainActivity : ComponentActivity() {

    private val deepLinkViewModel: DeepLinkViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        deepLinkViewModel.onDeeplinkData(intent.dataString)

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
                val rootNavigator = rememberNavigator(
                    initialKey = WelcomeKey(),
                    initialize = { it.deeplink(deepLinkViewModel) }
                ) { rootNavigation() }

                CompositionLocalProvider(
                    LocalRootNavigator provides rootNavigator,
                    LocalNavHostViewModelStoreOwner provides requireNotNull(LocalViewModelStoreOwner.current)
                ) {
                    rootNavigator.NavContainer(
                        bottomSheetOptions = sampleBottomSheetOptions()
                    )
                }

                LaunchedEffect(deepLinkViewModel.destinations) {
                    rootNavigator.deeplink(deepLinkViewModel)
                }
            }
        }
    }

    private fun NavigatorBuilderScope.rootNavigation() {
        welcomeNavigation()
        bottomTabNavigation()
        settingsNavigation()
        blockingBottomSheetNavigation()
        defaultTransition { _, _ -> MaterialSharedAxisTransitionXY }
    }

    private fun Navigator.deeplink(deepLinkViewModel: DeepLinkViewModel) {
        deepLinkViewModel.destinations
            .filterIsInstance<MainDestination>()
            .forEach { destination ->
                when (destination) {
                    MainDestination.BottomNav -> {
                        if (!popTo<BottomNavKey>()) {
                            setRoot(BottomNavKey())
                        }
                    }

                    MainDestination.Settings -> {
                        navigate(SettingsKey())
                    }
                }
            }
        deepLinkViewModel.onMainDestinationsHandled()
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        deepLinkViewModel.onDeeplinkData(intent?.dataString)
    }
}

package com.roudikk.navigator.sample

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color
import androidx.core.view.WindowCompat
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.roudikk.navigator.compose.NavContainer
import com.roudikk.navigator.rememberNavigator
import com.roudikk.navigator.sample.navigation.LocalDefaultNavigator
import com.roudikk.navigator.sample.navigation.LocalNavHostViewModelStoreOwner
import com.roudikk.navigator.sample.ui.composables.sampleBottomSheetOptions
import com.roudikk.navigator.sample.ui.screens.bottomnav.bottomTabNavigation
import com.roudikk.navigator.sample.ui.screens.dialogs.blockingBottomSheetNavigation
import com.roudikk.navigator.sample.ui.screens.settings.settingsNavigation
import com.roudikk.navigator.sample.ui.screens.welcome.WelcomeKey
import com.roudikk.navigator.sample.ui.screens.welcome.welcomeNavigation
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
                val defaultNavigator = rememberNavigator(initialKey = WelcomeKey()) {
                    welcomeNavigation()
                    bottomTabNavigation()
                    settingsNavigation()
                    blockingBottomSheetNavigation()
                }

                CompositionLocalProvider(
                    LocalDefaultNavigator provides defaultNavigator,
                    LocalNavHostViewModelStoreOwner provides requireNotNull(LocalViewModelStoreOwner.current)
                ) {
                    defaultNavigator.NavContainer(
                        bottomSheetOptions = sampleBottomSheetOptions()
                    )
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        viewModel.onNewIntent(intent?.dataString)
    }
}

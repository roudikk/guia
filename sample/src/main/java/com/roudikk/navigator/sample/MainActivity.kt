package com.roudikk.navigator.sample

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.core.view.WindowCompat
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.roudikk.navigator.NavContainer
import com.roudikk.navigator.NavHost
import com.roudikk.navigator.NavigationConfig
import com.roudikk.navigator.Navigator
import com.roudikk.navigator.sample.ui.composables.defaultBottomSheetSetup
import com.roudikk.navigator.sample.ui.screens.welcome.WelcomeScreen
import com.roudikk.navigator.sample.ui.theme.AppTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)

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
                NavHost(
                    Navigator.defaultKey to NavigationConfig.SingleStack(WelcomeScreen()),
                    AppNavigator.BottomTab.setup,
                    AppNavigator.NestedTab.setup
                ) {
                    NavContainer(bottomSheetSetup = defaultBottomSheetSetup())
                }
            }
        }
    }
}

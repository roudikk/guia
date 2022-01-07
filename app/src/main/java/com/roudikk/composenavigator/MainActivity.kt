package com.roudikk.composenavigator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.ui.graphics.Color
import androidx.core.view.WindowCompat
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.roudikk.compose_navigator.NavContainer
import com.roudikk.compose_navigator.NavHost
import com.roudikk.compose_navigator.NavigationConfig
import com.roudikk.composenavigator.ui.screens.welcome.WelcomeScreen
import com.roudikk.composenavigator.ui.theme.AppTheme

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
                NavHost(navigationConfig = NavigationConfig.SingleStack(WelcomeScreen())) {

                    NavContainer()
                }
            }
        }
    }
}

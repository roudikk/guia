package com.roudikk.navigator.sample.ui.screens.welcome

import android.content.res.Configuration
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.roudikk.navigator.NavigationKey
import com.roudikk.navigator.Navigator
import com.roudikk.navigator.NavigatorRulesScope
import com.roudikk.navigator.compose.LocalNavigationAnimation
import com.roudikk.navigator.compose.requireNavigator
import com.roudikk.navigator.navigate
import com.roudikk.navigator.sample.R
import com.roudikk.navigator.sample.ui.composables.NavigationAnimationPreview
import com.roudikk.navigator.sample.ui.screens.bottomnav.BottomTabKey
import com.roudikk.navigator.sample.ui.theme.AppTheme
import com.roudikk.navigator.setRoot
import kotlinx.parcelize.Parcelize

@Parcelize
class WelcomeKey : NavigationKey

fun NavigatorRulesScope.welcomeNavigation() {
    screen<WelcomeKey> { WelcomeScreen()  }
}

@Composable
private fun WelcomeScreen(
    navigator: Navigator = requireNavigator()
) {
    val composition by rememberLottieComposition(
        LottieCompositionSpec.RawRes(R.raw.welcome_animation)
    )
    val progress by animateLottieCompositionAsState(
        composition,
        iterations = LottieConstants.IterateForever
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .navigationBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier.weight(1f),
            contentAlignment = Alignment.Center
        ) {
            LottieAnimation(
                composition = composition,
                progress = progress
            )
        }

        with(LocalNavigationAnimation.current) {
            Button(
                modifier = Modifier
                    .widthIn(min = 300.dp)
                    .animateEnterExit(
                        enter = slideInVertically(tween(durationMillis = 600)) { it },
                        exit = slideOutVertically { it }
                    ),
                onClick = {
                    navigator.navigate(BottomTabKey())
                }
            ) {
                Text(text = "Navigate Home")
            }

            Button(
                modifier = Modifier
                    .animateEnterExit(
                        enter = slideInVertically(tween(durationMillis = 600)) { it },
                        exit = slideOutVertically { it }
                    )
                    .padding(horizontal = 16.dp)
                    .padding(top = 4.dp, bottom = 16.dp)
                    .widthIn(min = 300.dp),
                onClick = {
                    navigator.setRoot(BottomTabKey())
                }
            ) {
                Text(text = "Set Root Home")
            }
        }
    }
}

@Preview(
    device = Devices.PIXEL_3
)
@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    device = Devices.PIXEL_3
)
@Composable
private fun WelcomeContentPreviewDark() = AppTheme {
    NavigationAnimationPreview {
//        WelcomeScreen(navigator = rememberNavigator())
    }
}

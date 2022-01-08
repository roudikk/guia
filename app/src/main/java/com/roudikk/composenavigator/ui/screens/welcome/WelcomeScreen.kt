package com.roudikk.composenavigator.ui.screens.welcome

import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.*
import com.google.accompanist.insets.navigationBarsPadding
import com.roudikk.compose_navigator.NavOptions
import com.roudikk.compose_navigator.Screen
import com.roudikk.compose_navigator.findNavigator
import com.roudikk.composenavigator.AppPreview
import com.roudikk.composenavigator.MaterialSharedAxisTransitionXY
import com.roudikk.composenavigator.R
import com.roudikk.composenavigator.ui.screens.bottomnav.BottomNavScreen
import kotlinx.parcelize.Parcelize

@Parcelize
class WelcomeScreen : Screen {

    @Composable
    override fun Content(animatedVisibilityScope: AnimatedVisibilityScope) {
        WelcomeContent(animatedVisibilityScope)
    }
}

@Composable
private fun WelcomeContent(animatedVisibilityScope: AnimatedVisibilityScope) {
    val navigator = findNavigator()

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

        with(animatedVisibilityScope) {
            Button(
                modifier = Modifier
                    .widthIn(min = 300.dp)
                    .animateEnterExit(
                        enter = slideInVertically(tween(durationMillis = 600)) { it },
                        exit = slideOutVertically { it }
                    ),
                onClick = {
                    navigator.navigate(
                        BottomNavScreen(),
                        navOptions = NavOptions(
                            navTransition = MaterialSharedAxisTransitionXY
                        )
                    )
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
                    navigator.setRoot(
                        BottomNavScreen(),
                        navOptions = NavOptions(
                            navTransition = MaterialSharedAxisTransitionXY
                        )
                    )
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
@Composable
private fun WelcomeContentPreview() = AppPreview {
    AnimatedVisibility(visible = true) {
        WelcomeContent(this)
    }
}

@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    device = Devices.PIXEL_3
)
@Composable
private fun WelcomeContentPreviewDark() = AppPreview {
    AnimatedVisibility(visible = true) {
        WelcomeContent(this)
    }
}
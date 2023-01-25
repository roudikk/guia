package com.roudikk.navigator.sample.feature.welcome

import android.content.res.Configuration
import androidx.compose.animation.ExperimentalAnimationApi
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
import com.roudikk.navigator.animation.NavigationVisibilityScope
import com.roudikk.navigator.extensions.push
import com.roudikk.navigator.extensions.requireLocalNavigator
import com.roudikk.navigator.extensions.setRoot
import com.roudikk.navigator.sample.feature.bottomnav.api.BottomNavKey
import com.roudikk.navigator.sample.feature.common.composables.NavigationAnimationPreview
import com.roudikk.navigator.sample.feature.common.theme.AppTheme

@Composable
internal fun WelcomeScreen() {
    val navigator = requireLocalNavigator()

    WelcomeContent(
        onNavigateBottomNav = { navigator.push(BottomNavKey()) },
        onSetRooBottomNav = { navigator.setRoot(BottomNavKey()) },
    )
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun WelcomeContent(
    onNavigateBottomNav: () -> Unit = {},
    onSetRooBottomNav: () -> Unit = {}
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
                progress = { progress }
            )
        }

        NavigationVisibilityScope {
            Button(
                modifier = Modifier
                    .widthIn(min = 300.dp)
                    .animateEnterExit(
                        enter = slideInVertically(tween(durationMillis = 600)) { it },
                        exit = slideOutVertically { it }
                    ),
                onClick = onNavigateBottomNav
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
                onClick = onSetRooBottomNav
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
        WelcomeContent()
    }
}

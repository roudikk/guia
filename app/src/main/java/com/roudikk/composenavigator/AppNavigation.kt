package com.roudikk.composenavigator

import android.os.Parcel
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.accompanist.insets.systemBarsPadding
import com.roudikk.compose_navigator.*
import com.roudikk.compose_navigator.animation.*
import com.roudikk.composenavigator.ui.theme.AppTheme
import kotlinx.parcelize.Parcelize

sealed class AppNavigationKey {

    @Parcelize
    object Home : NavigationKey()

    @Parcelize
    object Nested : NavigationKey()

    @Parcelize
    object Dialogs : NavigationKey()

    @Parcelize
    object NavigationTree : NavigationKey()
}


val MaterialSharedAxisTransitionX = NavTransition(
    enter = navigationSlideInHorizontally { (it * 0.2f).toInt() }
            + navigationFadeIn(animationSpec = navigationTween(300)),

    exit = navigationSlideOutHorizontally { -(it * 0.1f).toInt() }
            + navigationFadeOut(animationSpec = navigationTween(150)),

    popEnter = navigationSlideInHorizontally { -(it * 0.1f).toInt() }
            + navigationFadeIn(animationSpec = navigationTween(300)),

    popExit = navigationSlideOutHorizontally { (it * 0.2f).toInt() }
            + navigationFadeOut(animationSpec = navigationTween(150))
)

@OptIn(ExperimentalAnimationApi::class)
val MaterialSharedAxisTransitionXY = NavTransition(
    enter = navigationFadeIn(animationSpec = navigationTween(300))
            + navigationScaleIn(initialScale = 0.8f, animationSpec = navigationTween(300)),

    exit = navigationScaleOut(targetScale = 1.1f, animationSpec = navigationTween(300))
            + navigationFadeOut(animationSpec = navigationTween(durationMillis = 150)),

    popEnter = navigationFadeIn(animationSpec = navigationTween(durationMillis = 300))
            + navigationScaleIn(initialScale = 1.1f, animationSpec = navigationTween(300)),

    popExit = navigationScaleOut(targetScale = 0.8f, animationSpec = navigationTween(300))
            + navigationFadeOut(animationSpec = navigationTween(durationMillis = 150))
)

fun defaultBottomSheetSetup(modifier: Modifier = Modifier) = BottomSheetSetup(
    bottomSheetContainer = { content ->
        BottomSheetSurface(
            modifier = Modifier
                .systemBarsPadding(bottom = false)
                .then(modifier),
            content = content
        )
    }
)

@Composable
fun BottomSheetSurface(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Surface(
        modifier = modifier
            .widthIn(max = 600.dp),
        tonalElevation = 16.dp,
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
    ) {
        content()
    }
}

@Composable
fun AppPreview(content: @Composable () -> Unit) = AppTheme {
    NavHost(navigationConfig = NavigationConfig.SingleStack(object : Screen {
        @Composable
        override fun Content(animatedVisibilityScope: AnimatedVisibilityScope) {
        }

        override fun describeContents() = error("Preview only")
        override fun writeToParcel(p0: Parcel?, p1: Int) = error("Preview only")
    })) {
        content()
    }
}
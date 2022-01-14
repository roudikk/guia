package com.roudikk.navigator.sample

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
import com.roudikk.navigator.*
import com.roudikk.navigator.animation.*
import com.roudikk.navigator.sample.ui.screens.dialogs.DialogsScreen
import com.roudikk.navigator.sample.ui.screens.home.HomeScreen
import com.roudikk.navigator.sample.ui.screens.navigation_tree.NavigationTreeScreen
import com.roudikk.navigator.sample.ui.screens.nested.NestedScreen
import com.roudikk.navigator.sample.ui.screens.nested.ParentNestedScreen
import com.roudikk.navigator.sample.ui.theme.AppTheme
import kotlinx.parcelize.Parcelize

sealed class AppNavigator(
    val key: String,
    val navigationConfig: NavigationConfig
) {

    val setup: Pair<String, NavigationConfig>
        get() = key to navigationConfig

    object BottomTab : AppNavigator(
        key = "Bottom Tab Navigator",
        navigationConfig = with(
            listOf(
                NavigationConfig.MultiStack.NavigationStackEntry(
                    key = AppNavigationKey.Home,
                    initialNavigationNode = HomeScreen()
                ),
                NavigationConfig.MultiStack.NavigationStackEntry(
                    key = AppNavigationKey.Nested,
                    initialNavigationNode = ParentNestedScreen()
                ),
                NavigationConfig.MultiStack.NavigationStackEntry(
                    key = AppNavigationKey.Dialogs,
                    initialNavigationNode = DialogsScreen()
                ),
                NavigationConfig.MultiStack.NavigationStackEntry(
                    key = AppNavigationKey.NavigationTree,
                    initialNavigationNode = NavigationTreeScreen()
                )
            )
        ) {
            NavigationConfig.MultiStack(
                entries = this,
                initialStackKey = this[0].key,
                backStackStrategy = BackStackStrategy.BackToInitialStack()
            )
        }
    )

    object NestedTab : AppNavigator(
        key = "Nested Tab Navigator",
        navigationConfig = NavigationConfig.SingleStack(NestedScreen(1))
    )
}

sealed class AppNavigationKey : NavigationKey() {

    @Parcelize
    object Home : AppNavigationKey()

    @Parcelize
    object Nested : AppNavigationKey()

    @Parcelize
    object Dialogs : AppNavigationKey()

    @Parcelize
    object NavigationTree : AppNavigationKey()
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

val VerticalSlideTransition = NavTransition(
    enter = navigationSlideInVertically { it / 2 }
            + navigationFadeIn(),
    exit = navigationSlideOutVertically { -it / 2 }
            + navigationFadeOut(),
    popEnter = navigationSlideInVertically { -it / 2 }
            + navigationFadeIn(),
    popExit = navigationSlideOutVertically { it / 2 }
            + navigationFadeOut()
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
    NavHost(
        "preview-navigator" to NavigationConfig.SingleStack(object : Screen {
            @Composable
            override fun AnimatedVisibilityScope.Content() {
            }

            override fun describeContents() = error("Preview only")
            override fun writeToParcel(p0: Parcel?, p1: Int) = error("Preview only")
        })
    ) {
        content()
    }
}
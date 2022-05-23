package com.roudikk.navigator.sample.navigation

import com.roudikk.navigator.animation.to
import com.roudikk.navigator.animation.transitions.navFadeIn
import com.roudikk.navigator.animation.transitions.navFadeOut
import com.roudikk.navigator.core.BackStackStrategy
import com.roudikk.navigator.core.NavigationConfig
import com.roudikk.navigator.sample.ui.screens.dialogs.DialogsScreen
import com.roudikk.navigator.sample.ui.screens.home.HomeScreen
import com.roudikk.navigator.sample.ui.screens.navigation_tree.NavigationTreeScreen
import com.roudikk.navigator.sample.ui.screens.nested.NestedScreen
import com.roudikk.navigator.sample.ui.screens.nested.ParentNestedScreen
import com.roudikk.navigator.sample.ui.screens.welcome.WelcomeScreen

object SampleNavConfig {

    val Default: NavigationConfig
        get() = NavigationConfig.SingleStack(
            defaultTransition = MaterialSharedAxisTransitionXY,
            initialNavigationNode = WelcomeScreen()
        )

    val BottomTab: NavigationConfig
        get() = with(
            listOf(
                NavigationConfig.MultiStack.NavigationStackEntry(
                    key = SampleStackKey.Home,
                    initialNavigationNode = HomeScreen()
                ),
                NavigationConfig.MultiStack.NavigationStackEntry(
                    key = SampleStackKey.Nested,
                    initialNavigationNode = ParentNestedScreen()
                ),
                NavigationConfig.MultiStack.NavigationStackEntry(
                    key = SampleStackKey.Dialogs,
                    initialNavigationNode = DialogsScreen()
                ),
                NavigationConfig.MultiStack.NavigationStackEntry(
                    key = SampleStackKey.StackTree,
                    initialNavigationNode = NavigationTreeScreen()
                )
            )
        ) {
            NavigationConfig.MultiStack(
                entries = this,
                initialStackKey = this[0].key,
                backStackStrategy = BackStackStrategy.BackToInitialStack,
                defaultTransition = MaterialSharedAxisTransitionX,
                stackEnterExitTransition = navFadeIn() to navFadeOut()
            )
        }

    val Nested: NavigationConfig
        get() = NavigationConfig.SingleStack(
            initialNavigationNode = NestedScreen(1),
            defaultTransition = VerticalSlideTransition
        )
}

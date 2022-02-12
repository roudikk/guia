package com.roudikk.navigator.sample.ui.deeplink

import android.content.Intent
import com.roudikk.navigator.NavOptions
import com.roudikk.navigator.NavigationNode
import com.roudikk.navigator.Navigator
import com.roudikk.navigator.deeplink.DeepLinkHandler
import com.roudikk.navigator.sample.AppNavigationKey
import com.roudikk.navigator.sample.AppNavigator
import com.roudikk.navigator.sample.MaterialSharedAxisTransitionX
import com.roudikk.navigator.sample.VerticalSlideTransition
import com.roudikk.navigator.sample.ui.screens.bottomnav.BottomNavScreen
import com.roudikk.navigator.sample.ui.screens.details.DetailsScreen
import com.roudikk.navigator.sample.ui.screens.nested.NestedScreen

class SampleDeepLinkHandler : DeepLinkHandler() {

    override fun handleIntent(navigator: (String) -> Navigator, intent: Intent?) {
        val defaultNavigator = navigator(Navigator.defaultKey)
        val bottomTabNavigator = navigator(AppNavigator.BottomTab.key)
        val nestedNavigator = navigator(AppNavigator.NestedTab.key)

        val data = intent?.data ?: return

        if (defaultNavigator.currentNodeKey != NavigationNode.key<BottomNavScreen>()) {
            defaultNavigator.setRoot(navigationNode = BottomNavScreen())
        }

        when (data.pathSegments.getOrNull(0)) {
            "home" -> {
                bottomTabNavigator.navigateToStack(AppNavigationKey.Home)
                (data.pathSegments).forEachIndexed { index, segment ->
                    when (segment) {
                        "details" -> {
                            val detailsId = data.pathSegments.getOrNull(index + 1) ?: return
                            bottomTabNavigator.navigate(
                                navigationNode = DetailsScreen(detailsId),
                                navOptions = NavOptions(
                                    navTransition = MaterialSharedAxisTransitionX
                                )
                            )
                        }
                    }
                }
            }
            "nested" -> {
                bottomTabNavigator.navigateToStack(AppNavigationKey.Nested)
                val nestedIndex = data.pathSegments.getOrNull(1)?.toIntOrNull()
                    ?: return
                nestedNavigator.navigate(
                    navigationNode = NestedScreen(nestedIndex),
                    navOptions = NavOptions(navTransition = VerticalSlideTransition)
                )
            }
        }
    }
}

package com.roudikk.guia.sample.feature.bottomnav

import com.roudikk.guia.core.NavigatorConfigBuilder
import com.roudikk.guia.sample.feature.bottomnav.navigation.BottomNavKey

fun NavigatorConfigBuilder.bottomNavNavigation(
    homeNavigation: NavigatorConfigBuilder.() -> Unit,
    nestedNavigation: NavigatorConfigBuilder.() -> Unit,
    dialogsNavigation: NavigatorConfigBuilder.() -> Unit,
    customNavigation: NavigatorConfigBuilder.() -> Unit
) {
    screen<BottomNavKey> {
        BottomNavScreen(
            homeNavigation = homeNavigation,
            nestedNavigation = nestedNavigation,
            dialogsNavigation = dialogsNavigation,
            customNavigation = customNavigation
        )
    }
}

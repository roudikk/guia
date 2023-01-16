package com.roudikk.navigator.sample.feature.bottomnav

import com.roudikk.navigator.core.NavigatorConfigBuilder
import com.roudikk.navigator.sample.feature.bottomnav.api.BottomNavKey

fun NavigatorConfigBuilder.bottomNavNavigation(
    homeNavigation: NavigatorConfigBuilder.() -> Unit,
    nestedNavigation: NavigatorConfigBuilder.() -> Unit,
    dialogsNavigation: NavigatorConfigBuilder.() -> Unit,
    customNavigation: NavigatorConfigBuilder.() -> Unit,
    navigationTreeNavigation: NavigatorConfigBuilder.() -> Unit,
) {
    screen<BottomNavKey> {
        BottomNavScreen(
            homeNavigation = homeNavigation,
            nestedNavigation = nestedNavigation,
            dialogsNavigation = dialogsNavigation,
            customNavigation = customNavigation,
            navigationTreeNavigation = navigationTreeNavigation
        )
    }
}

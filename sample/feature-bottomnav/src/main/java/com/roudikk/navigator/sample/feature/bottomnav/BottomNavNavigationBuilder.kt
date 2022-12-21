package com.roudikk.navigator.sample.feature.bottomnav

import com.roudikk.navigator.core.NavigatorConfigScope
import com.roudikk.navigator.sample.feature.bottomnav.api.BottomNavKey

fun NavigatorConfigScope.bottomNavNavigation(
    homeNavigation: NavigatorConfigScope.() -> Unit,
    nestedNavigation: NavigatorConfigScope.() -> Unit,
    dialogsNavigation: NavigatorConfigScope.() -> Unit,
    navigationTreeNavigation: NavigatorConfigScope.() -> Unit,
) {
    screen<BottomNavKey> {
        BottomNavScreen(
            homeNavigation = homeNavigation,
            nestedNavigation = nestedNavigation,
            dialogsNavigation = dialogsNavigation,
            navigationTreeNavigation = navigationTreeNavigation
        )
    }
}

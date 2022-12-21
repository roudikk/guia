package com.roudikk.navigator.sample.feature.bottomnav

import com.roudikk.navigator.core.NavigatorBuilderScope
import com.roudikk.navigator.sample.feature.bottomnav.api.BottomNavKey

fun NavigatorBuilderScope.bottomNavNavigation(
    homeNavigation: NavigatorBuilderScope.() -> Unit,
    nestedNavigation: NavigatorBuilderScope.() -> Unit,
    dialogsNavigation: NavigatorBuilderScope.() -> Unit,
    navigationTreeNavigation: NavigatorBuilderScope.() -> Unit,
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

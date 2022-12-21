package com.roudikk.navigator.sample.feature.navtree

import com.roudikk.navigator.core.NavigatorConfigScope
import com.roudikk.navigator.sample.feature.navtree.api.NavigationTreeKey

fun NavigatorConfigScope.navigationTreeNavigation() {
    screen<NavigationTreeKey> { NavigationTreeScreen() }
}

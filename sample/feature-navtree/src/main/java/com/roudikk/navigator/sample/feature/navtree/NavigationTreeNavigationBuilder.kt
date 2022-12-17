package com.roudikk.navigator.sample.feature.navtree

import com.roudikk.navigator.NavigatorBuilderScope
import com.roudikk.navigator.sample.feature.navtree.api.NavigationTreeKey

fun NavigatorBuilderScope.navigationTreeNavigation() {
    screen<NavigationTreeKey> { NavigationTreeScreen() }
}

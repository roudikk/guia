package com.roudikk.navigator.sample.feature.navtree

import com.roudikk.navigator.core.NavigatorBuilderScope
import com.roudikk.navigator.sample.feature.navtree.api.NavigationTreeKey

fun NavigatorBuilderScope.navigationTreeNavigation() {
    screen<NavigationTreeKey> { NavigationTreeScreen() }
}

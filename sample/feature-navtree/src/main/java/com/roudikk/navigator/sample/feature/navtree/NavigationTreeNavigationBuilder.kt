package com.roudikk.navigator.sample.feature.navtree

import com.roudikk.navigator.core.NavigatorConfigBuilder
import com.roudikk.navigator.sample.feature.navtree.api.NavigationTreeKey

fun NavigatorConfigBuilder.navigationTreeNavigation() {
    screen<NavigationTreeKey> { NavigationTreeScreen() }
}

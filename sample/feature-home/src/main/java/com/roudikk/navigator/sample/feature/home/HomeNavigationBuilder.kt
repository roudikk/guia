package com.roudikk.navigator.sample.feature.home

import com.roudikk.navigator.core.NavigatorBuilderScope
import com.roudikk.navigator.sample.feature.common.navigation.MaterialSharedAxisTransitionX
import com.roudikk.navigator.sample.feature.home.api.HomeKey

fun NavigatorBuilderScope.homeNavigation() {
    defaultTransition { -> MaterialSharedAxisTransitionX }
    screen<HomeKey> { HomeScreen() }
}

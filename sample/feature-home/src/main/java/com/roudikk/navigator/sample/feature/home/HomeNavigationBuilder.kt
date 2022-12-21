package com.roudikk.navigator.sample.feature.home

import com.roudikk.navigator.core.NavigatorConfigScope
import com.roudikk.navigator.sample.feature.common.navigation.MaterialSharedAxisTransitionX
import com.roudikk.navigator.sample.feature.home.api.HomeKey

fun NavigatorConfigScope.homeNavigation() {
    defaultTransition { -> MaterialSharedAxisTransitionX }
    screen<HomeKey> { HomeScreen() }
}

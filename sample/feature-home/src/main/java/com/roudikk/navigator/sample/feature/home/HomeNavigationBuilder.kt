package com.roudikk.navigator.sample.feature.home

import com.roudikk.navigator.core.NavigatorConfigBuilder
import com.roudikk.navigator.sample.feature.common.navigation.MaterialSharedAxisTransitionX
import com.roudikk.navigator.sample.feature.home.api.HomeKey

fun NavigatorConfigBuilder.homeNavigation() {
    defaultTransition { -> MaterialSharedAxisTransitionX }
    screen<HomeKey> { HomeScreen() }
}

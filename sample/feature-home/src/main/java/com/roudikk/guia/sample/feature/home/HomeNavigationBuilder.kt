package com.roudikk.guia.sample.feature.home

import com.roudikk.guia.core.NavigatorConfigBuilder
import com.roudikk.guia.sample.feature.common.navigation.MaterialSharedAxisTransitionX
import com.roudikk.guia.sample.feature.home.api.HomeKey

fun NavigatorConfigBuilder.homeNavigation() {
    defaultTransition { -> MaterialSharedAxisTransitionX }
    screen<HomeKey> { HomeScreen() }
}

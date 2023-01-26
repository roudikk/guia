package com.roudikk.guia.sample.feature.home

import com.roudikk.guia.core.BottomSheet
import com.roudikk.guia.core.Dialog
import com.roudikk.guia.core.NavigatorConfigBuilder
import com.roudikk.guia.core.Screen
import com.roudikk.guia.sample.feature.common.navigation.CrossFadeTransition
import com.roudikk.guia.sample.feature.common.navigation.MaterialSharedAxisTransitionX
import com.roudikk.guia.sample.feature.common.navigation.VerticalSlideTransition
import com.roudikk.guia.sample.feature.home.api.HomeKey

fun NavigatorConfigBuilder.homeNavigation() {
    nodeTransition<Screen> { -> MaterialSharedAxisTransitionX }
    nodeTransition<BottomSheet> { -> CrossFadeTransition }
    nodeTransition<Dialog> { -> VerticalSlideTransition }
//    defaultTransition { -> MaterialSharedAxisTransitionX }
    screen<HomeKey> { HomeScreen() }
}

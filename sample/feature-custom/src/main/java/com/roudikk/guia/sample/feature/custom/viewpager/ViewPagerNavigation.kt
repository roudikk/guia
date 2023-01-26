package com.roudikk.guia.sample.feature.custom.viewpager

import com.roudikk.guia.core.NavigatorConfigBuilder
import com.roudikk.guia.sample.feature.custom.navigation.PageKey
import com.roudikk.guia.sample.feature.custom.navigation.ViewPagerRootKey
import com.roudikk.guia.sample.feature.custom.card.CardRootScreen

fun NavigatorConfigBuilder.viewPagerNavigation() {
    screen<ViewPagerRootKey> { ViewPagerRootScreen() }
    screen<PageKey> { CardRootScreen() }
}

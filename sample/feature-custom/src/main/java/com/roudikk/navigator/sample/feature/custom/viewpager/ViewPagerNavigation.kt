package com.roudikk.navigator.sample.feature.custom.viewpager

import com.roudikk.navigator.core.NavigatorConfigBuilder
import com.roudikk.navigator.sample.feature.custom.api.PageKey
import com.roudikk.navigator.sample.feature.custom.api.ViewPagerRootKey
import com.roudikk.navigator.sample.feature.custom.card.CardRootScreen

fun NavigatorConfigBuilder.viewPagerNavigation() {
    screen<ViewPagerRootKey> { ViewPagerRootScreen() }
    screen<PageKey> { CardRootScreen() }
}

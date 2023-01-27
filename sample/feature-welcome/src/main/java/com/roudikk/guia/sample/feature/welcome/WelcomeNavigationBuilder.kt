package com.roudikk.guia.sample.feature.welcome

import com.roudikk.guia.core.NavigatorConfigBuilder
import com.roudikk.guia.sample.feature.welcome.navigation.WelcomeKey

fun NavigatorConfigBuilder.welcomeNavigation() {
    screen<WelcomeKey> { WelcomeScreen() }
}

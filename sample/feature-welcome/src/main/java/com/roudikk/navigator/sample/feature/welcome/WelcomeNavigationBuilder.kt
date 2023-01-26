package com.roudikk.guia.sample.feature.welcome

import com.roudikk.guia.core.NavigatorConfigBuilder
import com.roudikk.guia.sample.feature.welcome.api.WelcomeKey

fun NavigatorConfigBuilder.welcomeNavigation() {
    screen<WelcomeKey> { WelcomeScreen() }
}

package com.roudikk.navigator.sample.feature.welcome

import com.roudikk.navigator.core.NavigatorConfigBuilder
import com.roudikk.navigator.sample.feature.welcome.api.WelcomeKey

fun NavigatorConfigBuilder.welcomeNavigation() {
    screen<WelcomeKey> { WelcomeScreen() }
}

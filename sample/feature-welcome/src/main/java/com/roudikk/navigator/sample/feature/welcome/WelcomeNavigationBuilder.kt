package com.roudikk.navigator.sample.feature.welcome

import com.roudikk.navigator.core.NavigatorConfigScope
import com.roudikk.navigator.sample.feature.welcome.api.WelcomeKey

fun NavigatorConfigScope.welcomeNavigation() {
    screen<WelcomeKey> { WelcomeScreen() }
}

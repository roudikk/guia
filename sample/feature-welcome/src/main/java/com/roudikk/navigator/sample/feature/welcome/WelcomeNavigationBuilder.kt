package com.roudikk.navigator.sample.feature.welcome

import com.roudikk.navigator.core.NavigatorBuilderScope
import com.roudikk.navigator.sample.feature.welcome.api.WelcomeKey

fun NavigatorBuilderScope.welcomeNavigation() {
    screen<WelcomeKey> { WelcomeScreen() }
}

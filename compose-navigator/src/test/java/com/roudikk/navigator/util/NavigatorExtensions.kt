package com.roudikk.navigator.util

import com.roudikk.navigator.core.NavigationKey
import com.roudikk.navigator.core.Navigator
import com.roudikk.navigator.core.NavigatorConfig
import com.roudikk.navigator.core.NavigatorResultManager

fun navigatorWithKey(navigationKey: NavigationKey) = Navigator(
    initialKey = navigationKey,
    navigatorConfig = NavigatorConfig(),
    resultManager = NavigatorResultManager()
)

package com.roudikk.navigator

import com.roudikk.navigator.core.NavigationKey
import com.roudikk.navigator.core.Navigator
import com.roudikk.navigator.core.NavigatorConfig
import com.roudikk.navigator.core.NavigatorResultManager

fun navigatorWithKey(navigationKey: NavigationKey) = Navigator(
    initialKey = navigationKey,
    saveableStateHolder = TestSaveableStateHolder(),
    navigatorConfig = NavigatorConfig(),
    resultManager = NavigatorResultManager()
)

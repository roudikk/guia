package com.roudikk.navigator.util

import com.roudikk.navigator.core.BackStackEntry
import com.roudikk.navigator.core.NavigationKey
import com.roudikk.navigator.core.Navigator
import com.roudikk.navigator.core.NavigatorConfig
import com.roudikk.navigator.core.NavigatorResultManager

fun testNavigator(
    navigationKey: NavigationKey,
    navigatorConfig: NavigatorConfig = NavigatorConfig(),
    resultManager: NavigatorResultManager = NavigatorResultManager()
) = Navigator(
    initialKey = navigationKey,
    navigatorConfig = navigatorConfig,
    resultManager = resultManager
)

fun Navigator.entryForKey(navigationKey: NavigationKey): BackStackEntry {
    return backStack.first { it.navigationKey == navigationKey }
}

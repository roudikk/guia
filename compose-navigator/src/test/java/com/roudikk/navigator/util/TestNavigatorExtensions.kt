package com.roudikk.navigator.util

import com.google.common.truth.Truth.assertThat
import com.roudikk.navigator.core.BackstackEntry
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

fun Navigator.entryForKey(navigationKey: NavigationKey): BackstackEntry {
    return backStack.first { it.navigationKey == navigationKey }
}

fun Navigator.assertKeys(keys: List<NavigationKey>) {
    assertThat(backStackKeys).isEqualTo(keys)
    assertThat(backStack.all { keys.contains(it.navigationKey) }).isTrue()
}

fun Navigator.assertKeys(vararg keys: NavigationKey) {
    assertKeys(keys.toList())
}

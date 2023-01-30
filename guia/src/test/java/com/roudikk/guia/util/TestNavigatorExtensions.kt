package com.roudikk.guia.util

import com.google.common.truth.Truth.assertThat
import com.roudikk.guia.core.BackstackEntry
import com.roudikk.guia.core.NavigationKey
import com.roudikk.guia.core.Navigator
import com.roudikk.guia.core.NavigatorConfig
import com.roudikk.guia.core.NavigatorResultManager
import com.roudikk.guia.core.ResultManager

fun testNavigator(
    navigationKey: NavigationKey,
    navigatorConfig: NavigatorConfig = NavigatorConfig(),
    resultManager: ResultManager = NavigatorResultManager()
) = Navigator(
    initialKey = navigationKey,
    navigatorConfig = navigatorConfig,
    resultManager = resultManager
)

fun Navigator.entryForKey(navigationKey: NavigationKey): BackstackEntry {
    return backstack.first { it.navigationKey == navigationKey }
}

fun Navigator.assertKeys(keys: List<NavigationKey>) {
    assertThat(backstackKeys).isEqualTo(keys)
    assertThat(backstack.all { keys.contains(it.navigationKey) }).isTrue()
}

fun Navigator.assertKeys(vararg keys: NavigationKey) {
    assertKeys(keys.toList())
}

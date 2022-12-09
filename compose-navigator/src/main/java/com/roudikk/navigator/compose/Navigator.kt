package com.roudikk.navigator.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisallowComposableCalls
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import com.roudikk.navigator.compose.savedstate.NavigatorSaver
import com.roudikk.navigator.core.NavigationKey
import com.roudikk.navigator.core.Navigator
import com.roudikk.navigator.core.NavigatorRules
import com.roudikk.navigator.core.NavigatorRulesBuilder

@Composable
fun rememberNavigator(
    initialKey: NavigationKey,
    initialize: @DisallowComposableCalls (Navigator) -> Unit = {},
    navigatorRulesBuilder: @DisallowComposableCalls NavigatorRulesBuilder.() -> Unit = {}
): Navigator {
    val saveableStateHolder = rememberSaveableStateHolder()
    val navigatorRules = remember {
        NavigatorRulesBuilder()
            .apply(navigatorRulesBuilder)
            .build()
    }

    return rememberSaveable(
        saver = NavigatorSaver(saveableStateHolder, navigatorRules)
    ) {
        Navigator(
            initialKey = initialKey,
            saveableStateHolder = saveableStateHolder,
            navigatorRules = navigatorRules
        ).apply(initialize)
    }
}

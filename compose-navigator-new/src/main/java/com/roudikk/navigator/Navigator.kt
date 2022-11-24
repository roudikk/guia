package com.roudikk.navigator

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisallowComposableCalls
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.SaveableStateHolder
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.runtime.setValue
import com.roudikk.navigator.compose.NavContainer
import com.roudikk.navigator.core.BottomSheet
import com.roudikk.navigator.core.Destination
import com.roudikk.navigator.core.Dialog
import com.roudikk.navigator.core.NavigationNode
import com.roudikk.navigator.core.Screen
import com.roudikk.navigator.savedstate.navigatorSaver

@Composable
fun rememberNavigator(
    initialKey: NavigationKey,
    navigatorRulesBuilder: @DisallowComposableCalls NavigatorRulesScope.() -> Unit
): Navigator {
    val saveableStateHolder = rememberSaveableStateHolder()
    val navigatorRules = remember { NavigatorRulesScope().apply(navigatorRulesBuilder).build() }

    return rememberSaveable(
        saver = navigatorSaver(saveableStateHolder, navigatorRules)
    ) {
        Navigator(
            saveableStateHolder = saveableStateHolder,
            navigatorRules = navigatorRules
        ).apply {
            setBackstack(initialKey)
        }
    }
}

/**
 * Main component of the navigation system.
 *
 * To start use one of the [rememberNavigator] functions to create a navigator instance.
 * To render the state of a [Navigator] use a [NavContainer].
 * To define a screen use one of [Screen], [Dialog] or [BottomSheet].
 */
class Navigator internal constructor(
    internal val saveableStateHolder: SaveableStateHolder,
    internal val navigatorRules: NavigatorRules
) {
    internal var destinationsMap = hashMapOf<NavigationKey, Destination>()
    private var navigationNodesMap = hashMapOf<String, NavigationNode>()

    var backStack by mutableStateOf(listOf<NavigationKey>())
        private set

    val destinations by derivedStateOf {
        backStack.forEach {
            destinationsMap.getOrPut(it) {
                Destination(navigationKey = it)
            }
        }
        destinationsMap.values.toList().sortedBy { backStack.indexOf(it.navigationKey) }
    }

    var overrideBackPress by mutableStateOf(true)

    fun navigationNode(destination: Destination) = navigationNodesMap.getOrPut(destination.id) {
        navigationNodeForKey(destination.navigationKey)
    }

    fun setBackstack(vararg navigationKeys: NavigationKey) {
        backStack = navigationKeys.toList()
    }

    fun setBackstack(navigationKeys: List<NavigationKey>) {
        setBackstack(*navigationKeys.toTypedArray())
    }
}

private fun NavigationKey.notFoundError(): String {
    return "NavigationKey: $this was not declared. " +
            "Call associate<MyKey, MyNavigationNode> {} inside your Navigator rules."
}

internal fun Navigator.navigationNodeForKey(
    navigationKey: NavigationKey
): NavigationNode {
    return navigatorRules.associations[navigationKey::class]?.invoke(navigationKey)
        ?: error(navigationKey.notFoundError())
}

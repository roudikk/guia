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
import com.roudikk.navigator.compose.animation.EnterExitTransition
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
    private var navigationNodesMap = hashMapOf<Destination, NavigationNode>()

    var backStack by mutableStateOf(listOf<NavigationKey>())
        private set

    val destinations by derivedStateOf {
        backStack.forEach {
            destinationsMap.getOrPut(it) {
                Destination(navigationKey = it)
            }
        }

        destinationsMap.keys
            .filter { it !in backStack }
            .forEach { destinationsMap.remove(it) }

        val destinations = destinationsMap.values
            .toList()
            .sortedBy { backStack.indexOf(it.navigationKey) }

        navigationNodesMap.keys
            .filter { it !in destinations }
            .forEach { navigationNodesMap.remove(it) }

        destinations
    }

    var overrideBackPress by mutableStateOf(true)

    var transition by mutableStateOf(EnterExitTransition.None)
        private set

    fun navigationNode(destination: Destination) = navigationNodesMap.getOrPut(destination) {
        if (destination.navigationKey is SimpleNavigationKey<*>) {
            destination.navigationKey.navigationNode()
        } else {
            navigationNodeForKey(destination.navigationKey)
        }
    }

    fun setBackstack(vararg navigationKeys: NavigationKey) {
        require(navigationKeys.isNotEmpty()) {
            "Backstack cannot be empty. Please pass at least one NavigationKey"
        }

        val currentKey = navigationKeys.last()
        val popping = backStack.contains(currentKey)

        if (backStack.isNotEmpty()) {
            transition = navigatorRules.transitions[currentKey::class]
                ?.invoke(backStack.last(), currentKey, popping)
                ?: navigatorRules.defaultTransition(backStack.last(), currentKey, popping)
        }

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

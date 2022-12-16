package com.roudikk.navigator.core

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.SaveableStateHolder
import androidx.compose.runtime.setValue
import com.roudikk.navigator.compose.animation.EnterExitTransition

class Navigator internal constructor(
    internal val initialKey: NavigationKey,
    internal val saveableStateHolder: SaveableStateHolder,
    internal val navigatorRules: NavigatorRules
) {
    internal var destinationsMap = hashMapOf<NavigationKey, Destination>()
    private var navigationNodesMap = hashMapOf<Destination, NavigationNode>()

    var backStack by mutableStateOf(listOf<NavigationKey>())
        private set

    internal var results = mutableStateMapOf<String, Any?>()

    internal val destinations by derivedStateOf {
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

    init {
        setBackstack(initialKey)
    }

    internal fun navigationNode(destination: Destination) =
        navigationNodesMap.getOrPut(destination) {
            if (destination.navigationKey is NavigationKey.WithNode<*>) {
                destination.navigationKey.navigationNode()
            } else {
                val navigationKey = destination.navigationKey
                return navigatorRules.associations[navigationKey::class]
                    ?.invoke(navigationKey)
                    ?: error(
                        "NavigationKey: $navigationKey was not declared. " +
                                "Call `screen/dialog/bottomSheet<MyKey> { MyComposable() }`" +
                                " inside your Navigator rules."
                    )
            }
        }

    fun setBackstack(
        vararg navigationKeys: NavigationKey,
        overrideTransition: EnterExitTransition? = null
    ) {
        require(navigationKeys.isNotEmpty()) {
            "Backstack cannot be empty. Please pass at least one NavigationKey"
        }

        val currentKey = navigationKeys.last()
        val popping = backStack.contains(currentKey)

        if (overrideTransition != null) {
            transition = overrideTransition
        } else if (backStack.isNotEmpty()) {
            transition = navigatorRules.transitions[currentKey::class]
                ?.invoke(backStack.last(), currentKey, popping)
                ?: navigatorRules.defaultTransition(backStack.last(), currentKey, popping)
        }

        backStack = navigationKeys.toList()
    }

    fun setBackstack(
        navigationKeys: List<NavigationKey>,
        overrideTransition: EnterExitTransition? = null
    ) {
        setBackstack(
            *navigationKeys.toTypedArray(),
            overrideTransition = overrideTransition
        )
    }

    fun result(key: String): Any? {
        return results[key]
    }

    fun pushResult(key: String, result: Any) {
        results[key] = result
    }

    fun clearResult(key: String) {
        results[key] = null
    }
}

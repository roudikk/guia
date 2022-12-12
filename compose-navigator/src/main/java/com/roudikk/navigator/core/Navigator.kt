package com.roudikk.navigator.core

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisallowComposableCalls
import androidx.compose.runtime.LaunchedEffect
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

    init {
        setBackstack(initialKey)
    }

    var overrideBackPress by mutableStateOf(true)

    var transition by mutableStateOf(EnterExitTransition.None)
        private set

    internal fun navigationNode(destination: Destination) =
        navigationNodesMap.getOrPut(destination) {
            if (destination.navigationKey is NavigationKey.WithNode<*>) {
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

    private val results = mutableStateMapOf<Any, Any?>()

    fun results(key: Any): Any? {
        return results[key]
    }

    fun pushResult(key: Any, result: Any) {
        results[key] = result
    }

    fun clearResult(key: Any) {
        results[key] = null
    }

    inline fun <reified Result : Any> results(): Result? {
        return results(Result::class.java.simpleName) as Result?
    }

    inline fun <reified Result : Any> pushResult(result: Result) {
        pushResult(Result::class.java.simpleName, result)
    }

    inline fun <reified Result : Any> clearResult() {
        clearResult(Result::class.java.simpleName)
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

@Composable
inline fun <reified Result : Any> Navigator.onResult(
    crossinline onResult: @DisallowComposableCalls (Result) -> Unit
) {
    val result = results<Result>()
    LaunchedEffect(result) {
        result?.let(onResult)
        clearResult<Result>()
    }
}

@Composable
fun Navigator.onResult(
    key: Any,
    onResult: @DisallowComposableCalls (Any) -> Unit
) {
    val result = results(key)
    LaunchedEffect(result) {
        result?.let(onResult)
        clearResult(key)
    }
}

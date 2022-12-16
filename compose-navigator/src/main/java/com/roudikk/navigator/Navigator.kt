package com.roudikk.navigator

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisallowComposableCalls
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.SaveableStateHolder
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.runtime.setValue
import com.roudikk.navigator.compose.animation.EnterExitTransition
import com.roudikk.navigator.compose.savedstate.navigatorSaver
import com.roudikk.navigator.core.NavigationEntry
import com.roudikk.navigator.core.NavigationKey
import com.roudikk.navigator.core.NavigationNode

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
        saver = navigatorSaver(saveableStateHolder, navigatorRules)
    ) {
        Navigator(
            initialKey = initialKey,
            saveableStateHolder = saveableStateHolder,
            navigatorRules = navigatorRules
        ).apply(initialize)
    }
}

class Navigator internal constructor(
    internal val initialKey: NavigationKey,
    internal val saveableStateHolder: SaveableStateHolder,
    internal val navigatorRules: NavigatorRules
) {
    internal var destinationsMap = hashMapOf<NavigationKey, NavigationEntry>()
    private var navigationNodesMap = hashMapOf<NavigationEntry, NavigationNode>()

    var backStack by mutableStateOf(listOf<NavigationKey>())
        private set

    internal var results = mutableStateMapOf<String, Any?>()

    internal val navigationEntries by derivedStateOf {
        backStack.forEach {
            destinationsMap.getOrPut(it) {
                NavigationEntry(navigationKey = it)
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

    internal var currentTransition by mutableStateOf(EnterExitTransition.None)
    var overrideNextTransition: EnterExitTransition? = null

    init {
        setBackstack(initialKey)
    }

    internal fun navigationNode(navigationEntry: NavigationEntry) =
        navigationNodesMap.getOrPut(navigationEntry) {
            if (navigationEntry.navigationKey is NavigationKey.WithNode<*>) {
                navigationEntry.navigationKey.navigationNode()
            } else {
                val navigationKey = navigationEntry.navigationKey
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
    ) {
        require(navigationKeys.isNotEmpty()) {
            "Backstack cannot be empty. Please pass at least one NavigationKey"
        }

        val currentKey = navigationKeys.last()
        val popping = backStack.contains(currentKey)

        if (overrideNextTransition != null) {
            currentTransition = overrideNextTransition!!
            overrideNextTransition = null
        } else if (backStack.isNotEmpty()) {
            currentTransition = navigatorRules.transitions[currentKey::class]
                ?.invoke(backStack.last(), currentKey, popping)
                ?: navigatorRules.defaultTransition(backStack.last(), currentKey, popping)
        }

        backStack = navigationKeys.toList()
    }

    fun setBackstack(
        navigationKeys: List<NavigationKey>,
    ) {
        setBackstack(*navigationKeys.toTypedArray())
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

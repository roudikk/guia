package com.roudikk.navigator.core

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
import com.roudikk.navigator.animation.EnterExitTransition
import com.roudikk.navigator.savedstate.navigatorSaver

/**
 * Creates a saveable instance of a [Navigator].
 *
 * @param initialKey, the initialKey to be added to the backstack.
 * @param initialize, initialize the navigator before it is rendered.
 * @param scope, scope for providing navigator rules, check [NavigatorBuilder].
 */
@Composable
fun rememberNavigator(
    initialKey: NavigationKey,
    initialize: @DisallowComposableCalls (Navigator) -> Unit = {},
    scope: @DisallowComposableCalls NavigatorBuilderScope.() -> Unit = {}
): Navigator {
    val saveableStateHolder = rememberSaveableStateHolder()
    val navigatorRules = remember {
        NavigatorBuilderScope()
            .apply(scope)
            .build()
    }

    return rememberSaveable(
        saver = navigatorSaver(saveableStateHolder, navigatorRules)
    ) {
        Navigator(
            initialKey = initialKey,
            saveableStateHolder = saveableStateHolder,
            navigatorBuilder = navigatorRules
        ).apply(initialize)
    }
}

/**
 * The main component used for navigation.
 *
 * 
 */
class Navigator internal constructor(
    internal val initialKey: NavigationKey,
    internal val saveableStateHolder: SaveableStateHolder,
    internal val navigatorBuilder: NavigatorBuilder
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
                return navigatorBuilder.presentations[navigationKey::class]
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
            currentTransition = navigatorBuilder.transitions[currentKey::class]
                ?.invoke(backStack.last(), currentKey, popping)
                ?: navigatorBuilder.defaultTransition(backStack.last(), currentKey, popping)
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

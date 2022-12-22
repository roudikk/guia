package com.roudikk.navigator.core

import androidx.activity.compose.BackHandler
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
import com.roudikk.navigator.animation.EnterExitTransition
import com.roudikk.navigator.containers.NavContainer
import com.roudikk.navigator.savedstate.navigatorSaver

/**
 * Creates a saveable instance of a [Navigator].
 *
 * @param initialKey, the initialKey to be added to the backstack.
 * @param initialize, initialize the navigator before it's returned.
 * @param builder, builder for providing navigator configuration, check [NavigatorConfig].
 */
@Composable
fun rememberNavigator(
    initialKey: NavigationKey,
    initialize: @DisallowComposableCalls (Navigator) -> Unit = {},
    builder: @DisallowComposableCalls NavigatorConfigBuilder.() -> Unit = {}
): Navigator {
    val saveableStateHolder = rememberSaveableStateHolder()
    val resultManager = rememberResultManager()
    val navigatorConfig = remember {
        NavigatorConfigBuilder()
            .apply(builder)
            .build()
    }

    return rememberSaveable(
        saver = navigatorSaver(
            saveableStateHolder = saveableStateHolder,
            navigatorConfig = navigatorConfig,
            resultManager = resultManager
        )
    ) {
        Navigator(
            initialKey = initialKey,
            saveableStateHolder = saveableStateHolder,
            navigatorConfig = navigatorConfig,
            resultManager = resultManager
        ).apply(initialize)
    }
}

/**
 * The main component used for navigation.
 *
 * The back stack can be updated using [setBackstack], for more conventional or complex
 * navigation operations check the extensions in NavigationExtensions, or create your own.
 *
 * @property overrideBackPress, enable or disable the current [BackHandler] used in the navigator's [NavContainer]
 * @property overrideNextTransition, use this to override the next transition used in the next [setBackstack] call.
 * After the back stack is set, this is reset back to null.
 * @property backStack, the current back stack. To update, use [setBackstack].
 */
class Navigator internal constructor(
    internal val initialKey: NavigationKey,
    internal val saveableStateHolder: SaveableStateHolder,
    internal val navigatorConfig: NavigatorConfig,
    resultManager: ResultManager
) : ResultManager by resultManager {

    var overrideBackPress by mutableStateOf(true)
    var overrideNextTransition: EnterExitTransition? = null
    var backStack by mutableStateOf(listOf<NavigationKey>())
        private set

    internal var currentTransition by mutableStateOf(EnterExitTransition.None)
    internal var navigationEntriesMap = hashMapOf<NavigationKey, NavigationEntry>()
    internal var navigationNodesMap = hashMapOf<NavigationEntry, NavigationNode>()

    internal val navigationEntries by derivedStateOf {
        // Create a navigation entry for each back stack key.
        backStack.forEach {
            navigationEntriesMap.getOrPut(it) {
                NavigationEntry(navigationKey = it)
            }
        }

        // Remove all navigation entries that don't have a corresponding key anymore.
        navigationEntriesMap.keys
            .filter { it !in backStack }
            .forEach { navigationEntriesMap.remove(it) }

        val navigationEntries = navigationEntriesMap.values
            .toList()
            .sortedBy { backStack.indexOf(it.navigationKey) }

        // Clear navigation nodes presentations that are no longer used.
        navigationNodesMap.keys
            .filter { it !in navigationEntries }
            .forEach { navigationNodesMap.remove(it) }

        navigationEntries
    }

    init {
        // Initialize the back stack with the initial key.
        setBackstack(initialKey)
    }

    fun setBackstack(vararg navigationKeys: NavigationKey) {
        setBackstack(navigationKeys.toList())
    }

    fun setBackstack(
        navigationKeys: List<NavigationKey>,
    ) {
        require(navigationKeys.isNotEmpty()) {
            "Backstack cannot be empty. Please pass at least one NavigationKey"
        }

        val currentKey = navigationKeys.last()
        val isPop = backStack.contains(currentKey)

        // If the current transition is being overridden, then we use that transition and set it back
        // to null, otherwise we check if the current backstack is not empty and get the appropriate
        // transition from previous back to new backstack.
        if (overrideNextTransition != null) {
            currentTransition = overrideNextTransition!!
            overrideNextTransition = null
        } else if (backStack.isNotEmpty()) {
            currentTransition = navigatorConfig.transitions[currentKey::class]
                ?.invoke(backStack.last(), currentKey, isPop)
                ?: navigatorConfig.defaultTransition(backStack.last(), currentKey, isPop)
        }

        backStack = navigationKeys
    }
}

internal fun Navigator.navigationNode(navigationEntry: NavigationEntry) =
    navigationNodesMap.getOrPut(navigationEntry) {
        if (navigationEntry.navigationKey is NavigationKey.WithNode<*>) {
            navigationEntry.navigationKey.navigationNode()
        } else {
            val navigationKey = navigationEntry.navigationKey
            return navigatorConfig.presentations[navigationKey::class]
                ?.invoke(navigationKey)
                ?: error(
                    "NavigationKey: $navigationKey was not declared. " +
                        "Call `screen/dialog/bottomSheet<MyKey> { MyComposable() }`" +
                        " inside your Navigator rules."
                )
        }
    }

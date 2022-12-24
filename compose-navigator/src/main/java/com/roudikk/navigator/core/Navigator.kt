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
import com.roudikk.navigator.extensions.currentKey
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
    var backStack by mutableStateOf(listOf<BackStackEntry>())
        private set
    val backStackKeys by derivedStateOf {
        backStack.map { it.navigationKey }
    }

    internal var currentTransition by mutableStateOf(EnterExitTransition.None)

    init {
        // Initialize the back stack with the initial key.
        setBackstack(initialKey.entry())
    }

    fun setBackstack(vararg entries: BackStackEntry) {
        setBackstack(entries.toList())
    }

    fun setBackstack(
        entries: List<BackStackEntry>,
    ) {
        require(entries.isNotEmpty()) {
            "Backstack cannot be empty. Please pass at least one BackStackEntry"
        }

        val newEntry = entries.last()
        val isPop = backStack.contains(newEntry)

        currentTransition = when {
            // If the current transition is being overridden, then we use that transition
            overrideNextTransition != null -> overrideNextTransition!!

            // We check if the current backstack is not empty and get the appropriate
            // transition from previous backstack to the new backstack.
            backStack.isNotEmpty() -> navigatorConfig.transitions[newEntry.navigationKey::class]
                ?.invoke(currentKey, newEntry.navigationKey, isPop)
                ?: navigatorConfig.defaultTransition(currentKey, newEntry.navigationKey, isPop)

            // Otherwise we don't show any transitions
            else -> EnterExitTransition.None
        }

        overrideNextTransition = null
        backStack = entries
    }
}

internal fun Navigator.navigationNode(backStackEntry: BackStackEntry) =
    backStackEntry.navigationKey.let { navigationKey ->
        if (navigationKey is NavigationKey.WithNode<*>) {
            navigationKey.navigationNode()
        } else {
            navigatorConfig.presentations[navigationKey::class]?.invoke(navigationKey)
                ?: error("No presentation found for $navigationKey, add one in NavigationConfig")
        }
    }

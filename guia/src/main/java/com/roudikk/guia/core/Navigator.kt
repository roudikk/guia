package com.roudikk.guia.core

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisallowComposableCalls
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.roudikk.guia.animation.EnterExitTransition
import com.roudikk.guia.containers.NavContainer
import com.roudikk.guia.savedstate.navigatorSaver
import kotlin.reflect.KClass

/**
 * Creates a saveable instance of a [Navigator].
 *
 * @param initialKey, the initialKey to be added to the backstack.
 * @param initialize, initialize the navigator before it's returned.
 * @param builder, builder for providing navigator configuration, check [NavigatorConfig].
 */
@Composable
fun rememberNavigator(
    initialKey: NavigationKey? = null,
    initialize: @DisallowComposableCalls (Navigator) -> Unit = {},
    builder: @DisallowComposableCalls NavigatorConfigBuilder.() -> Unit = {}
): Navigator {
    val resultManager = rememberResultManager()
    val navigatorConfig = remember {
        NavigatorConfigBuilder()
            .apply(builder)
            .build()
    }

    return rememberSaveable(
        saver = navigatorSaver(
            navigatorConfig = navigatorConfig,
            resultManager = resultManager
        )
    ) {
        Navigator(
            navigatorConfig = navigatorConfig,
            resultManager = resultManager
        ).apply {
            initialKey?.let { setBackstack(it.entry()) }
            initialize(this)
        }
    }
}

/**
 * The main component used for navigation.
 *
 * The back stack can be updated using [setBackstack], for more conventional or complex
 * navigation operations check the extensions in NavigationExtensions, or create your own.
 *
 * @property overrideBackPress, enable or disable the current [BackHandler] used in the navigator's [NavContainer]
 * @property overrideTransitions, use this to override the next transition used in the next [setBackstack] call.
 * After the back stack is set, this is reset back to null.
 * @property backstack, the current back stack. To update, use [setBackstack].
 * @property backstackKeys, the current back stack keys.
 */
class Navigator(
    internal val navigatorConfig: NavigatorConfig,
    resultManager: ResultManager
) : ResultManager by resultManager {

    constructor(
        initialKey: NavigationKey,
        navigatorConfig: NavigatorConfig,
        resultManager: ResultManager,
    ) : this(navigatorConfig, resultManager) {
        setBackstack(initialKey.entry())
    }

    @set:JvmName("setNavigatorBackstack")
    var backstack by mutableStateOf(listOf<BackstackEntry>())
        private set

    val backstackKeys by derivedStateOf { backstack.map { it.navigationKey } }

    var overrideBackPress by mutableStateOf(true)

    internal val navigationNodes = mutableMapOf<String, NavigationNode>()

    @PublishedApi
    internal val transitions = mutableStateMapOf<KClass<out NavigationNode>, EnterExitTransition>()

    @PublishedApi
    internal val overrideTransitions =
        mutableStateMapOf<KClass<out NavigationNode>, EnterExitTransition?>()

    init {
        navigatorConfig.supportedNavigationNodes.forEach { kClass ->
            transitions[kClass] = EnterExitTransition.None
        }
    }

    /**
     * Updates the current back stack.
     *
     * @param entries, the new back stack entries.
     */
    fun setBackstack(vararg entries: BackstackEntry) {
        setBackstack(entries.toList())
    }

    /**
     * Updates the current back stack.
     *
     * @param entries, the new back stack entries.
     */
    fun setBackstack(
        entries: List<BackstackEntry>,
    ) {
        val newEntry = entries.lastOrNull()

        if (newEntry != null) {
            val isPop = backstack.contains(newEntry)

            navigatorConfig.supportedNavigationNodes.forEach { kClass ->
                transitions[kClass] = getTransition(
                    previousEntry = backstack.lastOrNull { entry ->
                        optionalNode(entry)?.let { it::class } == kClass
                    },
                    newEntry = entries.lastOrNull { entry ->
                        optionalNode(entry)?.let { it::class } == kClass
                    },
                    overrideTransition = overrideTransitions[kClass],
                    isPop = isPop
                )
            }

            overrideTransitions.clear()
        }

        backstack = entries
    }
}

inline fun <reified Node : NavigationNode> Navigator.transition(): EnterExitTransition {
    return transitions[Node::class] ?: EnterExitTransition.None
}

inline fun <reified Node : NavigationNode> Navigator.overrideTransition(
    transition: EnterExitTransition
) {
    overrideTransitions[Node::class] = transition
}

private fun Navigator.getTransition(
    previousEntry: BackstackEntry?,
    newEntry: BackstackEntry?,
    overrideTransition: EnterExitTransition?,
    isPop: Boolean
): EnterExitTransition {
    return when {
        previousEntry == null || newEntry == null -> EnterExitTransition.None

        // If the current transition is being overridden, then we use that transition.
        overrideTransition != null -> overrideTransition

        // We check if the current backstack is not empty and get the appropriate
        // transition from previous backstack to the new backstack.
        backstack.isNotEmpty() -> navigatorConfig.transitions[newEntry.navigationKey::class]
            ?.invoke(previousEntry.navigationKey, newEntry.navigationKey, isPop)
            ?: navigatorConfig.defaultTransition(
                previousEntry.navigationKey,
                newEntry.navigationKey,
                isPop
            )

        // Otherwise we don't show any transition.
        else -> EnterExitTransition.None
    }
}

private fun Navigator.optionalNode(backstackEntry: BackstackEntry): NavigationNode? {
    return try {
        navigationNode(backstackEntry)
    } catch (_: Exception) {
        null
    }
}

/**
 * Returns the [NavigationNode] given a [BackstackEntry].
 *
 * If the entry's key is a [NavigationKey.WithNode] then the key itself provides its navigation node.
 * Otherwise the key must have a defined presentation inside [NavigatorConfig.presentations]
 *
 * @param backstackEntry, the entry that requires a navigation node.
 *
 * @throws IllegalStateException if there is not presentation defined in [NavigatorConfig.presentations]
 * for the type of [BackstackEntry] provided.
 */
fun Navigator.navigationNode(backstackEntry: BackstackEntry): NavigationNode {
    return navigationNodes.getOrPut(backstackEntry.id) {
        backstackEntry.navigationKey.let { navigationKey ->
            if (navigationKey is NavigationKey.WithNode<*>) {
                navigationKey.navigationNode()
            } else {
                navigatorConfig.presentations[navigationKey::class]?.invoke(navigationKey)
                    ?: error("No presentation found for $navigationKey, add one in NavigationConfig")
            }
        }
    }
}

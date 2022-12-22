@file:Suppress("UNCHECKED_CAST")

package com.roudikk.navigator.core

import androidx.compose.runtime.Composable
import com.roudikk.navigator.animation.EnterExitTransition
import com.roudikk.navigator.animation.NavigationTransition
import com.roudikk.navigator.extensions.NavigationNodeTransition
import com.roudikk.navigator.extensions.PresentationsMap
import com.roudikk.navigator.extensions.TransitionsMap
import kotlin.reflect.KClass

/**
 * A Navigator's configuration.
 *
 * @property presentations, defines how a [NavigationKey] is rendered using a [NavigationNode].
 * @property transitions, defines how transitions happen between [NavigationKey].
 * @property defaultTransition, the default transition
 */
class NavigatorConfig internal constructor(
    internal val presentations: PresentationsMap = hashMapOf(),
    internal val transitions: TransitionsMap = hashMapOf(),
    internal val defaultTransition: NavigationNodeTransition =
        { _, _, _ -> EnterExitTransition.None }
)

class NavigatorConfigBuilder internal constructor() {
    private val presentations: PresentationsMap = hashMapOf()
    private val transitions: TransitionsMap = hashMapOf()
    private var defaultTransition: NavigationNodeTransition =
        { _, _, _ -> EnterExitTransition.None }

    fun navigationNode(
        keyClass: KClass<NavigationKey>,
        navigationNodeBuilder: (NavigationKey) -> NavigationNode
    ) {
        presentations[keyClass] = navigationNodeBuilder
    }

    inline fun <reified Key : NavigationKey> navigationNode(
        noinline navigationNodeBuilder: (Key) -> NavigationNode
    ) {
        navigationNode(
            Key::class as KClass<NavigationKey>,
            navigationNodeBuilder as (NavigationKey) -> NavigationNode
        )
    }

    inline fun <reified Key : NavigationKey> screen(
        noinline content: @Composable (Key) -> Unit
    ) {
        navigationNode<Key> {
            Screen { content(it) }
        }
    }

    inline fun <reified Key : NavigationKey> dialog(
        dialogOptions: DialogOptions = DialogOptions(),
        noinline content: @Composable (Key) -> Unit
    ) {
        navigationNode<Key> {
            Dialog(dialogOptions) { content(it) }
        }
    }

    inline fun <reified Key : NavigationKey> bottomSheet(
        bottomSheetOptions: BottomSheetOptions = BottomSheetOptions(),
        noinline content: @Composable (Key) -> Unit
    ) {
        navigationNode<Key> {
            BottomSheet(bottomSheetOptions) { content(it) }
        }
    }

    fun transition(
        keyClass: KClass<NavigationKey>,
        transition: NavigationNodeTransition
    ) {
        transitions[keyClass] = transition
    }

    inline fun <reified Key : NavigationKey> transition(
        noinline transition: (previous: NavigationKey, new: Key, isPop: Boolean) -> EnterExitTransition
    ) {
        transition(
            keyClass = Key::class as KClass<NavigationKey>,
            transition = transition as NavigationNodeTransition
        )
    }

    inline fun <reified Key : NavigationKey> transition(
        noinline transition: (previous: NavigationKey, new: Key) -> NavigationTransition
    ) {
        transition<Key> { previous, new, isPop ->
            val navigationTransition = transition(previous, new as Key)
            if (isPop) navigationTransition.popEnterExit else navigationTransition.enterExit
        }
    }

    inline fun <reified Key : NavigationKey> transition(
        noinline transition: () -> NavigationTransition
    ) {
        transition<Key> { _, _, isPop ->
            val navigationTransition = transition()
            if (isPop) navigationTransition.popEnterExit else navigationTransition.enterExit
        }
    }

    fun defaultTransition(
        transition: NavigationNodeTransition
    ) {
        defaultTransition = transition
    }

    fun defaultTransition(
        transition: (previous: NavigationKey, new: NavigationKey) -> NavigationTransition
    ) {
        defaultTransition = { previous, new, isPop ->
            val navigationTransition = transition(previous, new)
            if (isPop) navigationTransition.popEnterExit else navigationTransition.enterExit
        }
    }

    fun defaultTransition(
        transition: () -> NavigationTransition
    ) {
        defaultTransition = { _, _, isPop ->
            val navigationTransition = transition()
            if (isPop) navigationTransition.popEnterExit else navigationTransition.enterExit
        }
    }

    internal fun build() = NavigatorConfig(
        presentations = presentations,
        transitions = transitions,
        defaultTransition = defaultTransition
    )
}

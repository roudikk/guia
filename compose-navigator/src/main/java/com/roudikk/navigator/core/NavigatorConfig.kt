@file:Suppress("UNCHECKED_CAST")

package com.roudikk.navigator.core

import androidx.compose.runtime.Composable
import com.roudikk.navigator.animation.EnterExitTransition
import com.roudikk.navigator.animation.NavigationTransition
import com.roudikk.navigator.extensions.Presentations
import com.roudikk.navigator.extensions.Transition
import com.roudikk.navigator.extensions.Transitions
import kotlin.reflect.KClass

/**
 * A Navigator's configuration.
 *
 * @property presentations, defines how a [NavigationKey] is rendered using a [NavigationNode].
 * @property transitions, defines how transitions happen between [NavigationKey].
 * @property defaultTransition, the default transition
 */
class NavigatorConfig internal constructor(
    internal val presentations: Presentations = hashMapOf(),
    internal val transitions: Transitions = hashMapOf(),
    internal val defaultTransition: Transition = { _, _, _ -> EnterExitTransition.None }
)

/**
 * Builder for [NavigatorConfig]
 */
class NavigatorConfigBuilder internal constructor() {
    private val presentations: Presentations = hashMapOf()
    private val transitions: Transitions = hashMapOf()
    private var defaultTransition: Transition = { _, _, _ -> EnterExitTransition.None }

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
        transition: Transition
    ) {
        transitions[keyClass] = transition
    }

    inline fun <reified Key : NavigationKey> transition(
        noinline transition: (previous: NavigationKey, new: Key, isPop: Boolean) -> EnterExitTransition
    ) {
        transition(
            keyClass = Key::class as KClass<NavigationKey>,
            transition = transition as Transition
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
        transition: Transition
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

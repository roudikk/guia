@file:Suppress("UNCHECKED_CAST")

package com.roudikk.navigator.core

import androidx.compose.runtime.Composable
import com.roudikk.navigator.compose.animation.EnterExitTransition
import com.roudikk.navigator.compose.animation.NavigationTransition
import kotlin.reflect.KClass

private typealias AssociationsMap = HashMap<KClass<NavigationKey>, (NavigationKey) -> NavigationNode>
private typealias TransitionsMap = HashMap<KClass<NavigationKey>, (previous: NavigationKey, new: NavigationKey, isPop: Boolean) -> EnterExitTransition>
private typealias DefaultTransition = (previous: NavigationKey, new: NavigationKey, isPop: Boolean) -> EnterExitTransition

internal class NavigatorRules(
    val associations: AssociationsMap = hashMapOf(),
    val transitions: TransitionsMap = hashMapOf(),
    val defaultTransition: DefaultTransition = { _, _, _ -> EnterExitTransition.None }
)

class NavigatorRulesBuilder {

    @PublishedApi
    internal val associations: AssociationsMap = hashMapOf()

    @PublishedApi
    internal val transitions: TransitionsMap = hashMapOf()

    private var defaultTransition: DefaultTransition = { _, _, _ -> EnterExitTransition.None }

    @PublishedApi
    internal inline fun <reified Key : NavigationKey> navigationNode(
        noinline navigationNodeBuilder: (Key) -> NavigationNode
    ) {
        associations[Key::class as KClass<NavigationKey>] =
            navigationNodeBuilder as (NavigationKey) -> NavigationNode
    }

    inline fun <reified Key : NavigationKey> screen(noinline content: @Composable (Key) -> Unit) {
        navigationNode<Key> {
            screenNode { content(it) }
        }
    }

    inline fun <reified Key : NavigationKey> dialog(
        dialogOptions: DialogOptions = DialogOptions(),
        noinline content: @Composable (Key) -> Unit
    ) {
        navigationNode<Key> {
            dialogNode(dialogOptions) { content(it) }
        }
    }

    inline fun <reified Key : NavigationKey> bottomSheet(
        bottomSheetOptions: BottomSheetOptions = BottomSheetOptions(),
        noinline content: @Composable (Key) -> Unit
    ) {
        navigationNode<Key> {
            bottomSheetNode(bottomSheetOptions) { content(it) }
        }
    }

    inline fun <reified Key : NavigationKey> transition(
        noinline transition: (previous: NavigationKey, new: Key, isPop: Boolean) -> EnterExitTransition
    ) {
        transitions[Key::class as KClass<NavigationKey>] =
            transition as (NavigationKey, NavigationKey, Boolean) -> EnterExitTransition
    }

    inline fun <reified Key : NavigationKey> transition(
        noinline transition: (previous: NavigationKey, new: Key) -> NavigationTransition
    ) {
        transitions[Key::class as KClass<NavigationKey>] = { previous, new, isPop ->
            val navigationTransition = transition(previous, new as Key)
            if (isPop) navigationTransition.popEnterExit else navigationTransition.enterExit
        }
    }

    inline fun <reified Key : NavigationKey> transition(
        noinline transition: () -> NavigationTransition
    ) {
        transitions[Key::class as KClass<NavigationKey>] = { previous, new, isPop ->
            val navigationTransition = transition()
            if (isPop) navigationTransition.popEnterExit else navigationTransition.enterExit
        }
    }

    fun defaultTransition(
        transition: (previous: NavigationKey, new: NavigationKey, isPop: Boolean) -> EnterExitTransition
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
        defaultTransition = { previous, new, isPop ->
            val navigationTransition = transition()
            if (isPop) navigationTransition.popEnterExit else navigationTransition.enterExit
        }
    }

    internal fun build() = NavigatorRules(
        associations = associations,
        transitions = transitions,
        defaultTransition = defaultTransition
    )
}

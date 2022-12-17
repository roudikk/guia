@file:Suppress("UNCHECKED_CAST")

package com.roudikk.navigator

import androidx.compose.runtime.Composable
import com.roudikk.navigator.animation.EnterExitTransition
import com.roudikk.navigator.animation.NavigationTransition
import com.roudikk.navigator.core.BottomSheetOptions
import com.roudikk.navigator.core.DialogOptions
import com.roudikk.navigator.core.NavigationKey
import com.roudikk.navigator.core.NavigationNode
import com.roudikk.navigator.core.bottomSheetNode
import com.roudikk.navigator.core.dialogNode
import com.roudikk.navigator.core.screenNode
import com.roudikk.navigator.extensions.AssociationsMap
import com.roudikk.navigator.extensions.NavigationNodeTransition
import com.roudikk.navigator.extensions.TransitionsMap
import kotlin.reflect.KClass

class NavigatorBuilder internal constructor(
    internal val associations: AssociationsMap = hashMapOf(),
    internal val transitions: TransitionsMap = hashMapOf(),
    internal val defaultTransition: NavigationNodeTransition =
        { _, _, _ -> EnterExitTransition.None }
)

class NavigatorBuilderScope internal constructor() {
    private val associations: AssociationsMap = hashMapOf()
    private val transitions: TransitionsMap = hashMapOf()
    private var defaultTransition: NavigationNodeTransition =
        { _, _, _ -> EnterExitTransition.None }

    fun navigationNode(
        keyClass: KClass<NavigationKey>,
        navigationNodeBuilder: (NavigationKey) -> NavigationNode
    ) {
        associations[keyClass] = navigationNodeBuilder
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

    fun transition(
        keyClass: KClass<NavigationKey>,
        transition: NavigationNodeTransition
    ) {
        transitions[keyClass] = transition
    }

    inline fun <reified Key : NavigationKey> transition(
        noinline transition: NavigationNodeTransition
    ) {
        transition(
            keyClass = Key::class as KClass<NavigationKey>,
            transition = transition
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

    internal fun build() = NavigatorBuilder(
        associations = associations,
        transitions = transitions,
        defaultTransition = defaultTransition
    )
}

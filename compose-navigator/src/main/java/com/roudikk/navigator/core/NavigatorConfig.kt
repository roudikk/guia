@file:Suppress("UNCHECKED_CAST")

package com.roudikk.navigator.core

import androidx.compose.runtime.Composable
import com.roudikk.navigator.animation.EnterExitTransition
import com.roudikk.navigator.animation.NavigationTransition
import com.roudikk.navigator.core.BottomSheet.BottomSheetOptions
import com.roudikk.navigator.core.Dialog.DialogOptions
import com.roudikk.navigator.extensions.Presentations
import com.roudikk.navigator.extensions.Transition
import com.roudikk.navigator.extensions.Transitions
import kotlin.reflect.KClass

private val defaultNavigationNodes = listOf(
    Screen::class,
    BottomSheet::class,
    Dialog::class
)

/**
 * A Navigator's configuration.
 *
 * @property presentations, defines how a [NavigationKey] is rendered using a [NavigationNode].
 * @property transitions, defines how transitions happen between [NavigationKey].
 * @property defaultTransition, the default transition
 * @property supportedNavigationNodes, the supported navigation nodes for transitions.
 */
class NavigatorConfig internal constructor(
    internal val presentations: Presentations = hashMapOf(),
    internal val transitions: Transitions = hashMapOf(),
    internal val defaultTransition: Transition = { _, _, _ -> EnterExitTransition.None },
    internal val supportedNavigationNodes: List<KClass<out NavigationNode>> = defaultNavigationNodes
)

/**
 * Builder for [NavigatorConfig]
 */
class NavigatorConfigBuilder internal constructor() {
    private val presentations: Presentations = hashMapOf()
    private val transitions: Transitions = hashMapOf()
    private var defaultTransition: Transition = { _, _, _ -> EnterExitTransition.None }
    private var supportedNavigationNodes: List<KClass<out NavigationNode>> = defaultNavigationNodes

    /**
     * Define a [NavigationNode] presentation between a type of [NavigationKey].
     *
     * @param keyClass, the class associated with a [NavigationNode].
     * @param navigationNodeBuilder, builder that returns a [NavigationNode] for a specific [keyClass].
     */
    fun navigationNode(
        keyClass: KClass<NavigationKey>,
        navigationNodeBuilder: (NavigationKey) -> NavigationNode
    ) {
        presentations[keyClass] = navigationNodeBuilder
    }

    /**
     * Define a [NavigationNode] presentation for a type of [Key].
     *
     * @param navigationNodeBuilder, builder that returns a [NavigationNode] for a type [Key].
     */
    inline fun <reified Key : NavigationKey> navigationNode(
        noinline navigationNodeBuilder: (Key) -> NavigationNode
    ) {
        navigationNode(
            Key::class as KClass<NavigationKey>,
            navigationNodeBuilder as (NavigationKey) -> NavigationNode
        )
    }

    /**
     * Define a [Screen] presentation for a type of [Key].
     *
     * @param content, composable content for a given [Key].
     */
    inline fun <reified Key : NavigationKey> screen(
        noinline content: @Composable (Key) -> Unit
    ) {
        navigationNode<Key> {
            Screen { content(it) }
        }
    }

    /**
     * Define a [Dialog] presentation for a type of [Key].
     *
     * @param dialogOptions, optional initial [DialogOptions] to initialize the [Dialog].
     * @param content, composable content for a given [Key].
     */
    inline fun <reified Key : NavigationKey> dialog(
        dialogOptions: DialogOptions = DialogOptions(),
        noinline content: @Composable (Key) -> Unit
    ) {
        navigationNode<Key> {
            Dialog(dialogOptions) { content(it) }
        }
    }

    /**
     * Define a [BottomSheet] presentation for a type of [Key].
     *
     * @param bottomSheetOptions, optional initial [BottomSheetOptions] to initialize a [BottomSheet].
     * @param content, composable content for a given [Key].
     */
    inline fun <reified Key : NavigationKey> bottomSheet(
        bottomSheetOptions: BottomSheetOptions = BottomSheetOptions(),
        noinline content: @Composable (Key) -> Unit
    ) {
        navigationNode<Key> {
            BottomSheet(bottomSheetOptions) { content(it) }
        }
    }

    /**
     * Define a [Transition] for a given [keyClass].
     *
     * @param keyClass, the type of key to associate a transition with.
     * @param transition, the transition for a given [keyClass]
     */
    fun transition(
        keyClass: KClass<NavigationKey>,
        transition: Transition
    ) {
        transitions[keyClass] = transition
    }

    /**
     * Define a transition for a given [Key].
     *
     * @param transition, lambda that returns a [EnterExitTransition], it's provided the previous key,
     * the new key and whether or not the current transition is a push/pop.
     */
    inline fun <reified Key : NavigationKey> transition(
        noinline transition: (previous: NavigationKey, new: Key, isPop: Boolean) -> EnterExitTransition
    ) {
        transition(
            keyClass = Key::class as KClass<NavigationKey>,
            transition = transition as Transition
        )
    }

    /**
     * Define a transition for a given [Key].
     *
     * @param transition, lambda that returns a [NavigationTransition], the full enterExit and popEnterExit
     * transitions between a given previous/new key.
     */
    inline fun <reified Key : NavigationKey> transition(
        noinline transition: (previous: NavigationKey, new: Key) -> NavigationTransition
    ) {
        transition<Key> { previous, new, isPop ->
            val navigationTransition = transition(previous, new)
            if (isPop) navigationTransition.popEnterExit else navigationTransition.enterExit
        }
    }

    /**
     * Define a transition for a given [Key].
     *
     * @param transition, lambda that returns a [NavigationTransition], the full enterExit and popEnterExit
     * transitions between two navigation keys.
     */
    inline fun <reified Key : NavigationKey> transition(
        noinline transition: () -> NavigationTransition
    ) {
        transition<Key> { _, _, isPop ->
            val navigationTransition = transition()
            if (isPop) navigationTransition.popEnterExit else navigationTransition.enterExit
        }
    }

    /**
     * Define the default fallback transition between two navigation keys.
     *
     * @param transition, the default Transition.
     */
    fun defaultTransition(
        transition: Transition
    ) {
        defaultTransition = transition
    }

    /**
     * Define the default fallback transition between two navigation keys.
     *
     * @param transition, the default Transition between previous/new key.
     */
    fun defaultTransition(
        transition: (previous: NavigationKey, new: NavigationKey) -> NavigationTransition
    ) {
        defaultTransition = { previous, new, isPop ->
            val navigationTransition = transition(previous, new)
            if (isPop) navigationTransition.popEnterExit else navigationTransition.enterExit
        }
    }

    /**
     * Define the default fallback transition between two navigation keys.
     *
     * @param transition, the default Transition between any two navigation keys.
     */
    fun defaultTransition(
        transition: () -> NavigationTransition
    ) {
        defaultTransition = { _, _, isPop ->
            val navigationTransition = transition()
            if (isPop) navigationTransition.popEnterExit else navigationTransition.enterExit
        }
    }

    fun supportedNavigationNodes(
        vararg navigationNodeClass: KClass<out NavigationNode>
    ) {
        supportedNavigationNodes = navigationNodeClass.toList()
    }

    /**
     * Builds a [NavigatorConfig].
     */
    internal fun build() = NavigatorConfig(
        presentations = presentations,
        transitions = transitions,
        defaultTransition = defaultTransition,
        supportedNavigationNodes = supportedNavigationNodes
    )
}

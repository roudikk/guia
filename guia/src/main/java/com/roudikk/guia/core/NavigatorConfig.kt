@file:Suppress("UNCHECKED_CAST")

package com.roudikk.guia.core

import androidx.compose.runtime.Composable
import com.roudikk.guia.animation.EnterExitTransition
import com.roudikk.guia.animation.NavTransition
import com.roudikk.guia.core.BottomSheet.BottomSheetOptions
import com.roudikk.guia.core.Dialog.DialogOptions
import com.roudikk.guia.extensions.KeyTransitions
import com.roudikk.guia.extensions.NodeTransitions
import com.roudikk.guia.extensions.Presentations
import com.roudikk.guia.extensions.Transition
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
 * @property keyTransitions, defines how transitions happen between [NavigationKey].
 * @property defaultTransition, the default transition
 * @property supportedNavigationNodes, the supported navigation nodes for transitions.
 */
class NavigatorConfig internal constructor(
    internal val presentations: Presentations = hashMapOf(),
    internal val keyTransitions: KeyTransitions = hashMapOf(),
    internal val nodeTransitions: NodeTransitions = hashMapOf(),
    internal val defaultTransition: Transition = { _, _, _ -> EnterExitTransition.None },
    internal val supportedNavigationNodes: List<KClass<out NavigationNode>> = defaultNavigationNodes
)

/**
 * Builder for [NavigatorConfig]
 */
class NavigatorConfigBuilder internal constructor() {

    @PublishedApi
    internal val presentations: Presentations = hashMapOf()

    @PublishedApi
    internal val keyTransitions: KeyTransitions = hashMapOf()

    @PublishedApi
    internal val nodeTransitions: NodeTransitions = hashMapOf()

    private var defaultTransition: Transition = { _, _, _ -> EnterExitTransition.None }

    private var supportedNavigationNodes: List<KClass<out NavigationNode>> = defaultNavigationNodes

    /**
     * Define a [NavigationNode] presentation for [NavigationKey] class.
     *
     * @param keyClass, the [NavigationKey] class.
     * @param navigationNodeBuilder, builder that returns a [NavigationNode] for a given [NavigationKey]
     */
    fun navigationNode(
        keyClass: KClass<out NavigationKey>,
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
            keyClass = Key::class,
            navigationNodeBuilder = navigationNodeBuilder as (NavigationKey) -> NavigationNode
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
     * Define a transition for a given [Node]
     */
    inline fun <reified Node : NavigationNode> nodeTransition(
        noinline transition: Transition
    ) {
        nodeTransitions[Node::class] = transition
    }

    /**
     * Define a transition for a given [Node]
     */
    inline fun <reified Node : NavigationNode> nodeTransition(
        noinline transition: () -> NavTransition
    ) {
        nodeTransitions[Node::class] = { _, _, isPop ->
            val navigationTransition = transition()
            if (isPop) navigationTransition.popEnterExit else navigationTransition.enterExit
        }
    }

    /**
     * Define a transition for a given [Key].
     *
     * @param transition, lambda that returns a [EnterExitTransition], it's provided the previous key,
     * the new key and whether or not the current transition is a push/pop.
     */
    inline fun <reified Key : NavigationKey> keyTransition(
        noinline transition: (previous: NavigationKey, new: Key, isPop: Boolean) -> EnterExitTransition
    ) {
        keyTransitions[Key::class] = transition as Transition
    }

    /**
     * Define a transition for a given [Key].
     *
     * @param transition, lambda that returns a [NavTransition], the full enterExit and popEnterExit
     * transitions between a given previous/new key.
     */
    inline fun <reified Key : NavigationKey> keyTransition(
        noinline transition: (previous: NavigationKey, new: Key) -> NavTransition
    ) {
        keyTransition<Key> { previous, new, isPop ->
            val navigationTransition = transition(previous, new)
            if (isPop) navigationTransition.popEnterExit else navigationTransition.enterExit
        }
    }

    /**
     * Define a transition for a given [Key].
     *
     * @param transition, lambda that returns a [NavTransition], the full enterExit and popEnterExit
     * transitions between two navigation keys.
     */
    inline fun <reified Key : NavigationKey> keyTransition(
        noinline transition: () -> NavTransition
    ) {
        keyTransition<Key> { _, _, isPop ->
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
        transition: (previous: NavigationKey, new: NavigationKey) -> NavTransition
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
        transition: () -> NavTransition
    ) {
        defaultTransition = { _, _, isPop ->
            val navigationTransition = transition()
            if (isPop) navigationTransition.popEnterExit else navigationTransition.enterExit
        }
    }

    /**
     * Define the supported navigation nodes the [Navigator] will generate transitions for.
     *
     * @see [Navigator.transitions].
     */
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
        keyTransitions = keyTransitions,
        nodeTransitions = nodeTransitions,
        defaultTransition = defaultTransition,
        supportedNavigationNodes = supportedNavigationNodes
    )
}

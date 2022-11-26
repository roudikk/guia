package com.roudikk.navigator

import androidx.compose.runtime.Composable
import com.roudikk.navigator.compose.animation.EnterExitTransition
import com.roudikk.navigator.compose.animation.NavigationTransition
import com.roudikk.navigator.core.BottomSheet
import com.roudikk.navigator.core.BottomSheetOptions
import com.roudikk.navigator.core.Dialog
import com.roudikk.navigator.core.DialogOptions
import com.roudikk.navigator.core.NavigationNode
import com.roudikk.navigator.core.Screen
import kotlin.reflect.KClass

class NavigatorRules(
    internal val associations: HashMap<KClass<NavigationKey>, (NavigationKey) -> NavigationNode> = hashMapOf(),
    internal val transitions: HashMap<KClass<NavigationKey>, (previous: NavigationKey, new: NavigationKey, isPop: Boolean) -> EnterExitTransition> = hashMapOf(),
    internal val defaultTransition: (previous: NavigationKey, new: NavigationKey, isPop: Boolean) -> EnterExitTransition = { _, _, _ -> EnterExitTransition.None }
)

@Suppress("UNCHECKED_CAST")
class NavigatorRulesScope {

    @PublishedApi
    internal val associations: HashMap<KClass<NavigationKey>, (NavigationKey) -> NavigationNode> =
        hashMapOf()

    @PublishedApi
    internal val transitions: HashMap<KClass<NavigationKey>, (previous: NavigationKey, new: NavigationKey, isPop: Boolean) -> EnterExitTransition> =
        hashMapOf()

    private var defaultTransition: (previous: NavigationKey, new: NavigationKey, isPop: Boolean) -> EnterExitTransition =
        { _, _, _ -> EnterExitTransition.None }

    inline fun <reified Key : NavigationKey> navigationNode(
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

    fun defaultTransition(
        transition: (previous: NavigationKey, new: NavigationKey) -> NavigationTransition
    ) {
        defaultTransition = { previous, new, isPop ->
            val navigationTransition = transition(previous, new)
            if (isPop) navigationTransition.popEnterExit else navigationTransition.enterExit
        }
    }

    fun defaultTransition(
        transition: (previous: NavigationKey, new: NavigationKey, isPop: Boolean) -> EnterExitTransition
    ) {
        defaultTransition = transition
    }

    fun build() = NavigatorRules(
        associations = associations,
        transitions = transitions,
        defaultTransition = defaultTransition
    )

    fun screenNode(content: @Composable () -> Unit) = object : Screen {

        @Composable
        override fun Content() = content()
    }

    fun dialogNode(
        dialogOptions: DialogOptions = DialogOptions(),
        content: @Composable () -> Unit
    ) = object : Dialog {

        override val dialogOptions: DialogOptions
            get() = dialogOptions

        @Composable
        override fun Content() = content()
    }

    fun bottomSheetNode(
        bottomSheetOptions: BottomSheetOptions = BottomSheetOptions(),
        content: @Composable () -> Unit
    ) = object : BottomSheet {

        override val bottomSheetOptions: BottomSheetOptions
            get() = bottomSheetOptions

        @Composable
        override fun Content() = content()
    }
}

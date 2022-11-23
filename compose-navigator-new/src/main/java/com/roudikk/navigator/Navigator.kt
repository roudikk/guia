package com.roudikk.navigator

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.SaveableStateHolder
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.runtime.setValue
import com.roudikk.navigator.compose.NavContainer
import com.roudikk.navigator.core.BottomSheet
import com.roudikk.navigator.core.Destination
import com.roudikk.navigator.core.Dialog
import com.roudikk.navigator.core.EmptyNavigationNode
import com.roudikk.navigator.core.NavigationNode
import com.roudikk.navigator.core.Screen
import com.roudikk.navigator.savedstate.navigatorSaver

/**
 * Remembers and returns a single stack [Navigator].
 *
 * @param initialNavigationNode, the initial navigation node to render.
 * If the navigator should start with an empty node to be replaced later, use [EmptyNavigationNode]
 * @param defaultTransition, default transition used when no transition is given when navigating.
 */
@Composable
fun rememberNavigator(
    initialNavigationNode: NavigationNode,
): Navigator {
    val saveableStateHolder = rememberSaveableStateHolder()
    return rememberSaveable(saver = navigatorSaver(saveableStateHolder)) {
        Navigator(
            saveableStateHolder = saveableStateHolder
        ).apply {
            setBackstack(Destination(initialNavigationNode))
        }
    }
}

/**
 * Main component of the navigation system.
 *
 * To start use one of the [rememberNavigator] functions to create a navigator instance.
 * To render the state of a [Navigator] use a [NavContainer].
 * To define a screen use one of [Screen], [Dialog] or [BottomSheet].
 */
class Navigator internal constructor(
    internal val saveableStateHolder: SaveableStateHolder,
    internal val navigatorRules: NavigatorRules
) {

    internal var backStack by mutableStateOf(listOf<Destination>())
        private set

    var overrideBackPress = mutableStateOf(true)

    fun setBackstack(vararg destinations: Destination) {
        backStack = destinations.toList()
    }

    fun setBackstack(destinations: List<Destination>) {
        setBackstack(*destinations.toTypedArray())
    }
}

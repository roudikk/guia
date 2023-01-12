package com.roudikk.navigator.extensions

import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import com.roudikk.navigator.core.BottomSheet
import com.roudikk.navigator.core.Dialog
import com.roudikk.navigator.core.NavigationNode
import com.roudikk.navigator.core.Screen

/**
 * The local navigation node that is hosting a certain composable.
 */
internal val LocalNavigationNode = compositionLocalOf<NavigationNode> {
    error("Must be called inside a Composable hosted in a navigation node.")
}

/**
 * Returns the current local [NavigationNode].
 *
 * @throws IllegalStateException if called inside a Composable not hosted by a [NavigationNode]
 */
@Composable
fun localNavigationNode(): NavigationNode {
    return checkNotNull(LocalNavigationNode.current) {
        "Must be called inside a Composable hosted in a navigation node."
    }
}

/**
 * Returns the current local [BottomSheet].
 *
 * @throws IllegalStateException if the navigation node is not a bottom sheet.
 */
@Composable
fun localBottomSheet() = checkNotNull(localNavigationNode() as? BottomSheet)

/**
 * Returns the current local [Dialog].
 *
 * @throws IllegalStateException if the navigation node is not a dialog.
 */
@Composable
fun localDialog() = checkNotNull(localNavigationNode() as? Dialog)

/**
 * Returns the current local [Screen].
 *
 * @throws IllegalStateException if the navigation node is not a screen.
 */
@Composable
fun localsScreen() = checkNotNull(localNavigationNode() as? Screen)

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
internal val LocalNavigationNode = compositionLocalOf<NavigationNode?> {
    error("Must be called inside a Composable hosted in a navigation node.")
}

/**
 * Returns the current local [NavigationNode] if one is avaialble.
 */
@Composable
fun localNavigationNode(): NavigationNode? = LocalNavigationNode.current

/**
 * Returns the current local [NavigationNode].
 *
 * @throws IllegalStateException if called inside a Composable not hosted by a [NavigationNode]
 */
@Composable
fun requireLocalNavigationNode(): NavigationNode {
    return checkNotNull(LocalNavigationNode.current) {
        "Must be called inside a Composable hosted in a navigation node."
    }
}

/**
 * Returns the current local [BottomSheet] if one is available.
 */
@Composable
fun localBottomSheet() = localNavigationNode() as? BottomSheet

/**
 * Returns the current local [BottomSheet].
 *
 * @throws IllegalStateException if the navigation node is not a bottom sheet.
 */
@Composable
fun requireLocalBottomSheet() = checkNotNull(localBottomSheet()) {
    "Must be called in a Composable hosted in a BottomSheet"
}

/**
 * Returns the current local [Dialog] if one is available.
 */
@Composable
fun localDialog() = localNavigationNode() as? Dialog

/**
 * Returns the current local [Dialog].
 *
 * @throws IllegalStateException if the navigation node is not a dialog.
 */
@Composable
fun requireLocalDialog() = checkNotNull(localDialog()) {
    "Must be called in a Composable hosted in a Dialog"
}

/**
 * Returns the current local [Screen] if one is available.
 */
@Composable
fun localScreen() = localNavigationNode() as? Screen

/**
 * Returns the current local [Screen].
 *
 * @throws IllegalStateException if the navigation node is not a screen.
 */
@Composable
fun requireLocalScreen() = checkNotNull(localScreen()) {
    "Must be called in a Composable hosted in a screen"
}

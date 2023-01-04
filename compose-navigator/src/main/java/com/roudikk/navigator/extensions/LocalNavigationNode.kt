package com.roudikk.navigator.extensions

import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import com.roudikk.navigator.core.BottomSheet
import com.roudikk.navigator.core.Dialog
import com.roudikk.navigator.core.NavigationNode
import com.roudikk.navigator.core.Screen

internal val LocalNavigationNode = compositionLocalOf<NavigationNode> {
    error("Must be called inside a Composable hosted in a navigation node.")
}

@Composable
fun requireNavigationNode(): NavigationNode {
    return checkNotNull(LocalNavigationNode.current) {
        "Must be called inside a Composable hosted in a navigation node."
    }
}

@Composable
fun requireBottomSheet() = requireNavigationNode() as BottomSheet

@Composable
fun requireDialog() = requireNavigationNode() as Dialog

@Composable
fun requireScreen() = requireNavigationNode() as Screen

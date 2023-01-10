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
fun localNavigationNode(): NavigationNode {
    return checkNotNull(LocalNavigationNode.current) {
        "Must be called inside a Composable hosted in a navigation node."
    }
}

@Composable
fun localBottomSheet() = localNavigationNode() as BottomSheet

@Composable
fun localDialog() = localNavigationNode() as Dialog

@Composable
fun localsScreen() = localNavigationNode() as Screen

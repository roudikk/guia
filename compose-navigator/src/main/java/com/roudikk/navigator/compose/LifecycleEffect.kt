@file:Suppress("unused")

package com.roudikk.navigator.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.roudikk.navigator.core.NavigationNode

/**
 * Lifecycle listener for a [NavigationNode]
 *
 * @param onEnter, called when the node enters composition, this can be called when the node is initially rendered
 * or when the node is revisited.
 * @param onResume, called when the [NavigationNode] is resumed. This is called right after [onEnter]
 * and when the activity is resumed.
 * @param onPause, called when the [NavigationNode] is paused. This is called right before [onExit]
 * and when the activity is paused.
 * @param onExit, called when the node leaves composition. This doesn't mean the node is necessarily
 * not going to be revisited.
 * @param onDestroy, called the node is completely destroyed, this means the node will never be
 * revisited again.
 */
@Composable
fun NavigationNode.LifecycleEffect(
    onEnter: () -> Unit = {},
    onResume: () -> Unit = {},
    onPause: () -> Unit = {},
    onExit: () -> Unit = {},
    onDestroy: () -> Unit = {}
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val lifecycleEventObserver = remember {
        LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> onResume()
                Lifecycle.Event.ON_PAUSE -> onPause()
                Lifecycle.Event.ON_DESTROY -> onDestroy()
                else -> Unit
            }
        }
    }

    DisposableEffect(key) {
        onEnter()
        lifecycleOwner.lifecycle.addObserver(lifecycleEventObserver)
        onDispose {
            onExit()
            lifecycleOwner.lifecycle.removeObserver(lifecycleEventObserver)
        }
    }
}

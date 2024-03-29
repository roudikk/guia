package com.roudikk.guia.backstack

import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect

/**
 * Using [BackHandler] doesn't work properly inside a NavContainer, somehow the order
 * of the back handlers gets messed up when the activity is stopped and then started. For now
 * ignoring the lifecycle owner results in the proper behavior.
 *
 * reference: https://issuetracker.google.com/issues/182284739
 */
@Composable
fun NavBackHandler(
    enabled: Boolean = true,
    onBack: () -> Unit
) {
    val backDispatcher = requireNotNull(LocalOnBackPressedDispatcherOwner.current)
        .onBackPressedDispatcher

    DisposableEffect(enabled) {
        val callback = object : OnBackPressedCallback(enabled) {
            override fun handleOnBackPressed() {
                onBack()
            }
        }
        backDispatcher.addCallback(callback)
        onDispose { callback.remove() }
    }
}

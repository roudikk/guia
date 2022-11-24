package com.roudikk.navigator.compose

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import com.roudikk.navigator.compose.backstack.BackStackEntry
import com.roudikk.navigator.compose.backstack.BackStackManager
import com.roudikk.navigator.compose.backstack.LocalProvider

val LocalNavigationAnimation = compositionLocalOf<AnimatedVisibilityScope> {
    error("Must be used inside a navigation node contained in a NavContainer")
}

@Composable
internal fun AnimatedVisibilityScope.NavigationEntry(
    backStackManager: BackStackManager,
    backStackEntry: BackStackEntry
) {
    CompositionLocalProvider(
        LocalNavigationAnimation provides this
    ) {
        backStackEntry.LocalProvider {
            val destination = backStackEntry.destination

            Box(modifier = Modifier.testTag(destination.navigationKey::class.simpleName!!)) {
                backStackManager.navigationNode(backStackEntry.destination).Content()
            }

            DisposableEffect(backStackManager, backStackEntry) {
                onDispose { backStackManager.onEntryDisposed() }
            }
        }
    }
}

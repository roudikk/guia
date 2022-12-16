package com.roudikk.navigator.compose

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import com.roudikk.navigator.compose.backstack.BackStackEntry
import com.roudikk.navigator.compose.backstack.BackStackManager
import com.roudikk.navigator.compose.backstack.LocalProvider

@Composable
internal fun NavigationEntry(
    backStackManager: BackStackManager,
    backStackEntry: BackStackEntry
) {
    backStackEntry.LocalProvider {
        val destination = backStackEntry.destination

        Box(modifier = Modifier.testTag(destination.navigationKey.tag())) {
            backStackManager.navigationNode(destination).Content()
        }

        DisposableEffect(backStackManager, backStackEntry) {
            onDispose { backStackManager.onEntryDisposed() }
        }
    }
}

package com.roudikk.navigator.containers

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import com.roudikk.navigator.backstack.LifecycleEntry
import com.roudikk.navigator.backstack.BackStackManager
import com.roudikk.navigator.backstack.LocalProvider
import com.roudikk.navigator.core.NavigationKey
import com.roudikk.navigator.core.Navigator
import com.roudikk.navigator.core.navigationNode

/**
 * Renders the [LifecycleEntry].
 *
 * Provides all necessary lifecycle management components using [LocalProvider].
 * Adds a [testTag] around the entry so it can be used in UI Tests. Check [NavigationKey.tag]
 * for usage.
 */
@Composable
internal fun Navigator.NavigationEntryContainer(
    backStackManager: BackStackManager,
    lifecycleEntry: LifecycleEntry
) = with(lifecycleEntry.backStackEntry) {
    lifecycleEntry.LocalProvider {
        Box(modifier = Modifier.testTag(navigationKey.tag())) {
            navigationNode(this@with).Content()
        }

        DisposableEffect(backStackManager, lifecycleEntry) {
            onDispose { backStackManager.onEntryDisposed() }
        }
    }
}

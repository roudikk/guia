package com.roudikk.navigator.containers

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import com.roudikk.navigator.backstack.BackStackManager
import com.roudikk.navigator.backstack.LifeCycleEntry
import com.roudikk.navigator.backstack.LocalProvider
import com.roudikk.navigator.backstack.VisibleBackStack
import com.roudikk.navigator.core.NavigationKey
import com.roudikk.navigator.core.Navigator
import com.roudikk.navigator.core.navigationNode
import com.roudikk.navigator.extensions.LocalNavigationNode

/**
 * Renders the [LifeCycleEntry].
 *
 * Provides all necessary lifecycle management components using [LocalProvider].
 * Adds a [testTag] around the entry so it can be used in UI Tests. Check [NavigationKey.tag]
 * for usage.
 */
@Composable
fun <VB : VisibleBackStack> Navigator.NavigationEntryContainer(
    backStackManager: BackStackManager<VB>,
    lifecycleEntry: LifeCycleEntry
) = with(lifecycleEntry.backStackEntry) {
    lifecycleEntry.LocalProvider {
        Box(modifier = Modifier.testTag(navigationKey.tag())) {
            val navigationNode = navigationNode(this@with)
            CompositionLocalProvider(
                LocalNavigationNode provides navigationNode,
                content = navigationNode.content
            )
        }

        DisposableEffect(backStackManager, lifecycleEntry) {
            onDispose { backStackManager.onEntryDisposed() }
        }
    }
}

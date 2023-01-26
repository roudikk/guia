package com.roudikk.navigator.containers

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import com.roudikk.navigator.backstack.manager.BackstackManager
import com.roudikk.navigator.backstack.LifecycleEntry
import com.roudikk.navigator.backstack.LocalProvider
import com.roudikk.navigator.backstack.VisibleBackstack
import com.roudikk.navigator.core.NavigationKey
import com.roudikk.navigator.core.Navigator
import com.roudikk.navigator.core.navigationNode
import com.roudikk.navigator.extensions.LocalNavigationNode

/**
 * Renders the [LifecycleEntry].
 *
 * Provides all necessary lifecycle management components using [LocalProvider].
 * Adds a [testTag] around the entry so it can be used in UI Tests. Check [NavigationKey.tag]
 * for usage.
 */
@Composable
fun <VB : VisibleBackstack> Navigator.NavigationEntryContainer(
    backstackManager: BackstackManager<VB>,
    lifecycleEntry: LifecycleEntry
) = with(lifecycleEntry.backstackEntry) {
    lifecycleEntry.LocalProvider {
        Box(modifier = Modifier.testTag(navigationKey.tag())) {
            val navigationNode = navigationNode(this@with)
            CompositionLocalProvider(
                LocalNavigationNode provides navigationNode,
                content = navigationNode.content
            )
        }

        DisposableEffect(backstackManager, lifecycleEntry) {
            onDispose { backstackManager.onEntryDisposed() }
        }
    }
}

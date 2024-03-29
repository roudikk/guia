package com.roudikk.guia.containers

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import com.roudikk.guia.backstack.RenderGroup
import com.roudikk.guia.core.NavigationKey
import com.roudikk.guia.core.Navigator
import com.roudikk.guia.core.navigationNode
import com.roudikk.guia.extensions.LocalNavigationNode
import com.roudikk.guia.extensions.LocalNavigator
import com.roudikk.guia.lifecycle.LifecycleEntry
import com.roudikk.guia.lifecycle.LifecycleManager
import com.roudikk.guia.lifecycle.LocalProvider

/**
 * Renders the [LifecycleEntry].
 *
 * Provides all necessary lifecycle management components using [LocalProvider].
 * Adds a [testTag] around the entry so it can be used in UI Tests. Check [NavigationKey.tag]
 * for usage.
 */
@Composable
fun <VB : RenderGroup> Navigator.NavEntryContainer(
    lifecycleManager: LifecycleManager<VB>,
    lifecycleEntry: LifecycleEntry
) = with(lifecycleEntry.backstackEntry) {
    lifecycleEntry.LocalProvider {
        Box(modifier = Modifier.testTag(navigationKey.tag())) {
            val navigationNode = navigationNode(this@with)
            CompositionLocalProvider(
                LocalNavigationNode provides navigationNode,
                LocalNavigator provides this@NavEntryContainer,
                content = navigationNode.content
            )
        }

        DisposableEffect(lifecycleManager, lifecycleEntry) {
            onDispose { lifecycleManager.onEntryDisposed() }
        }
    }
}

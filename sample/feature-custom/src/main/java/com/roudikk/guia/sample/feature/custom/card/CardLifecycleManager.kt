package com.roudikk.guia.sample.feature.custom.card

import androidx.compose.runtime.Composable
import androidx.lifecycle.Lifecycle
import com.roudikk.guia.backstack.RenderGroup
import com.roudikk.guia.core.Navigator
import com.roudikk.guia.lifecycle.LifecycleEntry
import com.roudikk.guia.lifecycle.LifecycleManager
import com.roudikk.guia.lifecycle.id
import com.roudikk.guia.lifecycle.rememberLifecycleManager

@Composable
internal fun rememberCardLifecycleManager(navigator: Navigator): LifecycleManager<VisibleCardStack> {
    return rememberLifecycleManager(
        navigator = navigator,
        getRenderGroup = { backstack, createEntry ->
            VisibleCardStack(
                backstack
                    .reversed()
                    .takeLast(2)
                    .map(createEntry)
            )
        },
        updateLifecycles = { renderGroup, entries ->
            entries.filter { it !in renderGroup.entries }
                .forEach { it.maxLifecycleState = Lifecycle.State.CREATED }

            renderGroup.entries.forEach {
                if (it.id == navigator.backstack.first().id) {
                    it.maxLifecycleState = Lifecycle.State.RESUMED
                } else {
                    it.maxLifecycleState = Lifecycle.State.STARTED
                }
            }
        }
    )
}

class VisibleCardStack(
    override val entries: List<LifecycleEntry>
) : RenderGroup

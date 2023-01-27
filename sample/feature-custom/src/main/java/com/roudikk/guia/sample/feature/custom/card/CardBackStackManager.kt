package com.roudikk.guia.sample.feature.custom.card

import androidx.compose.runtime.Composable
import androidx.lifecycle.Lifecycle
import com.roudikk.guia.backstack.LifecycleEntry
import com.roudikk.guia.backstack.VisibleBackstack
import com.roudikk.guia.backstack.id
import com.roudikk.guia.backstack.manager.BackstackManager
import com.roudikk.guia.backstack.manager.rememberBackstackManager
import com.roudikk.guia.core.Navigator

@Composable
internal fun rememberCardBackstackManager(navigator: Navigator): BackstackManager<VisibleCardStack> {
    return rememberBackstackManager(
        navigator = navigator,
        getVisibleBackstack = { backstack, createEntry ->
            VisibleCardStack(
                backstack
                    .reversed()
                    .takeLast(2)
                    .map(createEntry)
            )
        },
        updateLifecycles = { visibleBackstack, entries ->
            entries.filter { it !in visibleBackstack.entries }
                .forEach { it.maxLifecycleState = Lifecycle.State.CREATED }

            visibleBackstack.entries.forEach {
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
) : VisibleBackstack

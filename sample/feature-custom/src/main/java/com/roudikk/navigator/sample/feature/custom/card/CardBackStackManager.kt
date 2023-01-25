package com.roudikk.navigator.sample.feature.custom.card

import androidx.compose.runtime.Composable
import androidx.lifecycle.Lifecycle
import com.roudikk.navigator.backstack.LifecycleEntry
import com.roudikk.navigator.backstack.VisibleBackstack
import com.roudikk.navigator.backstack.id
import com.roudikk.navigator.backstack.manager.BackstackManager
import com.roudikk.navigator.backstack.manager.rememberBackstackManager
import com.roudikk.navigator.core.Navigator

@Composable
internal fun rememberCardBackstackManager(navigator: Navigator): BackstackManager<VisibleCardStack> {
    return rememberBackstackManager(
        navigator = navigator,
        getVisibleBackstack = { backStack, createEntry ->
            VisibleCardStack(
                backStack
                    .reversed()
                    .takeLast(2)
                    .map(createEntry)
            )
        },
        updateLifecycles = { visibleBackstack, entries ->
            entries.filter { it !in visibleBackstack.entries }
                .forEach { it.maxLifecycleState = Lifecycle.State.CREATED }

            visibleBackstack.entries.forEach {
                if (it.id == navigator.backStack.first().id) {
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

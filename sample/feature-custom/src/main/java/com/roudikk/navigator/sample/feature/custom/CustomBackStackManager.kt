package com.roudikk.navigator.sample.feature.custom

import androidx.compose.runtime.Composable
import androidx.lifecycle.Lifecycle
import com.roudikk.navigator.backstack.BackStackManager
import com.roudikk.navigator.backstack.LifeCycleEntry
import com.roudikk.navigator.backstack.VisibleBackStack
import com.roudikk.navigator.backstack.id
import com.roudikk.navigator.backstack.rememberBackStackManager
import com.roudikk.navigator.core.Navigator

@Composable
internal fun rememberCustomBackStackManager(navigator: Navigator): BackStackManager<CustomVisibleBackStack> {
    return rememberBackStackManager(
        navigator = navigator,
        getVisibleBackStack = { backStack, createEntry ->
            CustomVisibleBackStack(
                backStack
                    .reversed()
                    .takeLast(2)
                    .map(createEntry)
            )
        },
        updateLifeCycles = { visibleBackStack, lifeCycleEntries ->
            lifeCycleEntries.values.filter { it !in visibleBackStack.entries }
                .forEach { it.maxLifecycleState = Lifecycle.State.CREATED }

            visibleBackStack.entries.forEach {
                if (it.id == navigator.backStack.first().id) {
                    it.maxLifecycleState = Lifecycle.State.RESUMED
                } else {
                    it.maxLifecycleState = Lifecycle.State.STARTED
                }
            }
        }
    )
}

class CustomVisibleBackStack(
    override val entries: List<LifeCycleEntry>
) : VisibleBackStack

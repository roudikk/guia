package com.roudikk.navigator.sample.feature.custom.viewpager

import androidx.compose.runtime.Composable
import androidx.lifecycle.Lifecycle
import com.roudikk.navigator.backstack.BackStackManager
import com.roudikk.navigator.backstack.LifeCycleEntry
import com.roudikk.navigator.backstack.VisibleBackStack
import com.roudikk.navigator.backstack.id
import com.roudikk.navigator.backstack.rememberBackStackManager
import com.roudikk.navigator.core.Navigator
import com.roudikk.navigator.core.entry
import com.roudikk.navigator.sample.feature.custom.api.PageKey

@Composable
fun rememberViewPagerBackStackManager(navigator: Navigator): BackStackManager<ViewPagerVisibleStack> {
    return rememberBackStackManager(
        navigator = navigator,
        getVisibleBackStack = { backStack, createEntry ->
            val activeIndex = backStack.indexOfFirst { (it.navigationKey as PageKey).isActive }
            val left = backStack.getOrNull(activeIndex - 1)
            val center = backStack.getOrNull(activeIndex)
            val right = backStack.getOrNull(activeIndex + 1)
            ViewPagerVisibleStack(
                left = left?.let(createEntry),
                center = center?.let(createEntry),
                right = right?.let(createEntry)
            )
        },
        updateLifeCycles = { visibleBackStack, entries ->
            entries.filter { it !in visibleBackStack.entries }
                .forEach { it.maxLifecycleState = Lifecycle.State.CREATED }

            visibleBackStack.entries.forEach {
                if (it.id == visibleBackStack.center?.id) {
                    it.maxLifecycleState = Lifecycle.State.RESUMED
                } else {
                    it.maxLifecycleState = Lifecycle.State.STARTED
                }
            }
        }
    )
}

class ViewPagerVisibleStack(
    val left: LifeCycleEntry?,
    val center: LifeCycleEntry?,
    val right: LifeCycleEntry?
) : VisibleBackStack {

    override val entries: List<LifeCycleEntry> = listOfNotNull(left, center, right)
}

val Navigator.activeIndex: Int
    get() = backStack.indexOfFirst { (it.navigationKey as PageKey).isActive }
        .takeIf { it != -1 } ?: 0

fun Navigator.setActive(activeIndex: Int) {
    setBackstack(
        backStack.mapIndexed { index, entry ->
            entry.copy(navigationKey = PageKey(isActive = index == activeIndex))
        }
    )
}

fun Navigator.addPage() {
    val shouldSetActive = backStack.isEmpty()
    setBackstack(backStack + PageKey(isActive = shouldSetActive).entry())
}

fun Navigator.removePage() {
    val shouldSetPreviousActive = ((activeIndex == backStack.lastIndex) && backStack.size > 1)
    val newBackStack = if (shouldSetPreviousActive) {
        backStack.mapIndexed { index, entry ->
            if (index == backStack.lastIndex - 1) {
                entry.copy(navigationKey = PageKey(isActive = true))
            } else {
                entry
            }
        } - backStack.last()
    } else {
        (backStack - backStack.last())
    }
    setBackstack(newBackStack)
}

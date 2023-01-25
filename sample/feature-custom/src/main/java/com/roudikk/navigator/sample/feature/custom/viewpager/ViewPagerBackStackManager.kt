package com.roudikk.navigator.sample.feature.custom.viewpager

import androidx.compose.runtime.Composable
import androidx.lifecycle.Lifecycle
import com.roudikk.navigator.backstack.manager.BackstackManager
import com.roudikk.navigator.backstack.LifecycleEntry
import com.roudikk.navigator.backstack.VisibleBackstack
import com.roudikk.navigator.backstack.id
import com.roudikk.navigator.backstack.manager.rememberBackstackManager
import com.roudikk.navigator.core.Navigator
import com.roudikk.navigator.core.entry
import com.roudikk.navigator.sample.feature.custom.api.PageKey

@Composable
fun rememberViewPagerBackstackManager(navigator: Navigator): BackstackManager<ViewPagerVisibleStack> {
    return rememberBackstackManager(
        navigator = navigator,
        getVisibleBackstack = { backStack, createEntry ->
            val activeIndex = navigator.activeIndex
            val left = backStack.getOrNull(activeIndex - 1)
            val center = backStack.getOrNull(activeIndex)
            val right = backStack.getOrNull(activeIndex + 1)
            ViewPagerVisibleStack(
                left = left?.let(createEntry),
                center = center?.let(createEntry),
                right = right?.let(createEntry)
            )
        },
        updateLifecycles = { visibleBackstack, entries ->
            entries.filter { it !in visibleBackstack.entries }
                .forEach { it.maxLifecycleState = Lifecycle.State.CREATED }

            visibleBackstack.entries.forEach {
                if (it.id == visibleBackstack.center?.id) {
                    it.maxLifecycleState = Lifecycle.State.RESUMED
                } else {
                    it.maxLifecycleState = Lifecycle.State.STARTED
                }
            }
        }
    )
}

class ViewPagerVisibleStack(
    val left: LifecycleEntry?,
    val center: LifecycleEntry?,
    val right: LifecycleEntry?
) : VisibleBackstack {

    override val entries: List<LifecycleEntry> = listOfNotNull(left, center, right)
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
    val newBackstack = if (shouldSetPreviousActive) {
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
    setBackstack(newBackstack)
}

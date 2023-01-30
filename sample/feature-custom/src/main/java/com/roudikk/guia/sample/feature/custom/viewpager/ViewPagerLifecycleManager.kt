package com.roudikk.guia.sample.feature.custom.viewpager

import androidx.compose.runtime.Composable
import androidx.lifecycle.Lifecycle
import com.roudikk.guia.lifecycle.LifecycleManager
import com.roudikk.guia.lifecycle.LifecycleEntry
import com.roudikk.guia.backstack.RenderGroup
import com.roudikk.guia.lifecycle.id
import com.roudikk.guia.lifecycle.rememberLifecycleManager
import com.roudikk.guia.core.Navigator
import com.roudikk.guia.core.entry
import com.roudikk.guia.sample.feature.custom.navigation.PageKey

@Composable
fun rememberViewPagerLifecycleManager(navigator: Navigator): LifecycleManager<ViewPagerVisibleStack> {
    return rememberLifecycleManager(
        navigator = navigator,
        getRenderGroup = { backstack, createEntry ->
            val activeIndex = navigator.activeIndex
            val left = backstack.getOrNull(activeIndex - 1)
            val center = backstack.getOrNull(activeIndex)
            val right = backstack.getOrNull(activeIndex + 1)
            ViewPagerVisibleStack(
                left = left?.let(createEntry),
                center = center?.let(createEntry),
                right = right?.let(createEntry)
            )
        },
        updateLifecycles = { renderGroup, entries ->
            entries.filter { it !in renderGroup.entries }
                .forEach { it.maxLifecycleState = Lifecycle.State.CREATED }

            renderGroup.entries.forEach {
                if (it.id == renderGroup.center?.id) {
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
) : RenderGroup {

    override val entries: List<LifecycleEntry> = listOfNotNull(left, center, right)
}

val Navigator.activeIndex: Int
    get() = backstack.indexOfFirst { (it.navigationKey as PageKey).isActive }
        .takeIf { it != -1 } ?: 0

fun Navigator.setActive(activeIndex: Int) {
    setBackstack(
        backstack.mapIndexed { index, entry ->
            entry.copy(navigationKey = PageKey(isActive = index == activeIndex))
        }
    )
}

fun Navigator.addPage() {
    val shouldSetActive = backstack.isEmpty()
    setBackstack(backstack + PageKey(isActive = shouldSetActive).entry())
}

fun Navigator.removePage() {
    val shouldSetPreviousActive = ((activeIndex == backstack.lastIndex) && backstack.size > 1)
    val newBackstack = if (shouldSetPreviousActive) {
        backstack.mapIndexed { index, entry ->
            if (index == backstack.lastIndex - 1) {
                entry.copy(navigationKey = PageKey(isActive = true))
            } else {
                entry
            }
        } - backstack.last()
    } else {
        (backstack - backstack.last())
    }
    setBackstack(newBackstack)
}

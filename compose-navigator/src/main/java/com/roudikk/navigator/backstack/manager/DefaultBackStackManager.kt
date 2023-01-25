package com.roudikk.navigator.backstack.manager

import androidx.compose.runtime.Composable
import androidx.lifecycle.Lifecycle
import com.roudikk.navigator.backstack.DefaultVisibleBackstack
import com.roudikk.navigator.backstack.id
import com.roudikk.navigator.core.BottomSheet
import com.roudikk.navigator.core.Dialog
import com.roudikk.navigator.core.Navigator
import com.roudikk.navigator.core.Screen
import com.roudikk.navigator.core.navigationNode

@Composable
fun rememberDefaultBackstackManager(navigator: Navigator): BackstackManager<DefaultVisibleBackstack> {
    return rememberBackstackManager(
        navigator = navigator,
        /**
         * Update the current visible back stack.
         */
        /**
         * Update the current visible back stack.
         */
        getVisibleBackstack = { backStack, createLifecycleEntry ->
            val currentEntry = backStack.lastOrNull()
                ?: return@rememberBackstackManager DefaultVisibleBackstack()

            // Check if there's a valid screen that should be visible.
            // It's the last entry that is a screen.
            val screenEntry = backStack.lastOrNull {
                navigator.navigationNode(it) is Screen
            }?.let(createLifecycleEntry)

            // Check if there's a valid dialog that should be visible.
            // It must be the last entry in the backstack.
            val dialogEntry = currentEntry.takeIf {
                navigator.navigationNode(it) is Dialog
            }?.let(createLifecycleEntry)

            // Check if there's a valid bottom sheet that should be visible.
            // It's either the top most destination, in between a screen and a dialog
            // or just by itself.
            val bottomSheetEntry = backStack
                .lastOrNull { navigator.navigationNode(it) is BottomSheet }
                .takeIf {
                    if (currentEntry == it) return@takeIf true
                    val bottomSheetIndex = backStack.indexOf(it)
                    val entriesAfter = backStack.subList(bottomSheetIndex + 1, backStack.size)
                    entriesAfter.all { entry -> navigator.navigationNode(entry) is Dialog }
                }?.let(createLifecycleEntry)

            val visibleBackstack = DefaultVisibleBackstack(
                screenEntry = screenEntry,
                dialogEntry = dialogEntry,
                bottomSheetEntry = bottomSheetEntry
            )

            val navigatingToDialog = navigator.navigationNode(currentEntry) is Dialog &&
                backStack.getOrNull(backStack.lastIndex - 1)
                    ?.let(navigator::navigationNode) !is Dialog

            val navigatingToBottomSheet = navigator.navigationNode(currentEntry) is BottomSheet &&
                backStack.getOrNull(backStack.lastIndex - 1)
                    ?.let(navigator::navigationNode) !is BottomSheet

            visibleBackstack.entries.forEach {
                // If the current destination is a bottom sheet or a dialog
                // we need to pause whatever is behind it. In the case of a dialog
                // we might have a bottom sheet and/or a screen behind it, whereas a bottom sheet
                // would just have a screen behind it.
                if (navigatingToDialog || navigatingToBottomSheet) {
                    if (it.id == currentEntry.id) {
                        it.maxLifecycleState = Lifecycle.State.RESUMED
                    } else {
                        it.maxLifecycleState = Lifecycle.State.STARTED
                    }
                } else {
                    it.maxLifecycleState = Lifecycle.State.STARTED
                }
            }

            visibleBackstack
        },
        /**
         * Make sure all entries' lifecycle is up to date.
         *
         * All entries that are not in the current visibleBackstack will be in the destroyed state.
         *
         * We then check the entries that are in the visibleBackstack:
         * - If the entry is the current last entry in the [Navigator] backstack, it's resumed.
         * - If the entry is not the current last entry, then it's paused.
         */
        /**
         * Make sure all entries' lifecycle is up to date.
         *
         * All entries that are not in the current visibleBackstack will be in the destroyed state.
         *
         * We then check the entries that are in the visibleBackstack:
         * - If the entry is the current last entry in the [Navigator] backstack, it's resumed.
         * - If the entry is not the current last entry, then it's paused.
         */
        updateLifecycles = { visibleBackstack, lifeCycleEntries ->
            lifeCycleEntries.filter { it !in visibleBackstack.entries }
                .forEach { it.maxLifecycleState = Lifecycle.State.CREATED }

            visibleBackstack.entries.forEach {
                if (it.id == navigator.backStack.lastOrNull()?.id) {
                    it.maxLifecycleState = Lifecycle.State.RESUMED
                } else {
                    it.maxLifecycleState = Lifecycle.State.STARTED
                }
            }
        },
    )
}
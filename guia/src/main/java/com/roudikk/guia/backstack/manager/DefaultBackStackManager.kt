package com.roudikk.guia.backstack.manager

import androidx.compose.runtime.Composable
import androidx.lifecycle.Lifecycle
import com.roudikk.guia.backstack.DefaultVisibleBackstack
import com.roudikk.guia.backstack.id
import com.roudikk.guia.core.BottomSheet
import com.roudikk.guia.core.Dialog
import com.roudikk.guia.core.Navigator
import com.roudikk.guia.core.Screen
import com.roudikk.guia.core.navigationNode

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
        getVisibleBackstack = { backstack, createLifecycleEntry ->
            val currentEntry = backstack.lastOrNull()
                ?: return@rememberBackstackManager DefaultVisibleBackstack()

            // Check if there's a valid screen that should be visible.
            // It's the last entry that is a screen.
            val screenEntry = backstack.lastOrNull {
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
            val bottomSheetEntry = backstack
                .lastOrNull { navigator.navigationNode(it) is BottomSheet }
                .takeIf {
                    if (currentEntry == it) return@takeIf true
                    val bottomSheetIndex = backstack.indexOf(it)
                    val entriesAfter = backstack.subList(bottomSheetIndex + 1, backstack.size)
                    entriesAfter.all { entry -> navigator.navigationNode(entry) is Dialog }
                }?.let(createLifecycleEntry)

            val visibleBackstack = DefaultVisibleBackstack(
                screenEntry = screenEntry,
                dialogEntry = dialogEntry,
                bottomSheetEntry = bottomSheetEntry
            )

            val navigatingToDialog = navigator.navigationNode(currentEntry) is Dialog &&
                backstack.getOrNull(backstack.lastIndex - 1)
                    ?.let(navigator::navigationNode) !is Dialog

            val navigatingToBottomSheet = navigator.navigationNode(currentEntry) is BottomSheet &&
                backstack.getOrNull(backstack.lastIndex - 1)
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
                if (it.id == navigator.backstack.lastOrNull()?.id) {
                    it.maxLifecycleState = Lifecycle.State.RESUMED
                } else {
                    it.maxLifecycleState = Lifecycle.State.STARTED
                }
            }
        },
    )
}
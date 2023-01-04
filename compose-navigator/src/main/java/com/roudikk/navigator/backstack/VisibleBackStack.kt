package com.roudikk.navigator.backstack

import com.roudikk.navigator.core.BottomSheet
import com.roudikk.navigator.core.Dialog
import com.roudikk.navigator.core.Navigator
import com.roudikk.navigator.core.Screen

/**
 * Collection of the currently visible [LifeCycleEntry]s.
 *
 * A [VisibleBackStack] will be determined based on the current state of the [Navigator].
 *
 * - [screenEntry] will be the last entry with navigation node being a [Screen], if it exists.
 * - [dialogEntry] will be the last entry if it is a [Dialog], if it exists.
 * - [bottomSheetEntry] could be either the last entry if it's a [BottomSheet] or the entry
 *   right before that if the last entry is a [Dialog]
 *
 * For ex:
 * If the current back stack is:
 *  - [Screen], [BottomSheet], [Dialog]
 * Then all entries would be available.
 *
 * If the current back stack is:
 * - [Screen], [Screen], [Dialog], [BottomSheet], [Screen]
 * Then only [screenEntry] will be available
 *
 * If the current back stack is:
 * - [Dialog], [BottomSheet]
 * Then only [bottomSheetEntry] will be available
 *
 * If the current back stack is:
 * - [Screen], [Dialog]
 * Then [screenEntry] and [dialogEntry] will be available
 *
 * If the current back stack is:
 * - [Screen], [BottomSheet]
 * Then [screenEntry] and [bottomSheetEntry] will be available
 */
internal class VisibleBackStack(
    val screenEntry: LifeCycleEntry?,
    val dialogEntry: LifeCycleEntry?,
    val bottomSheetEntry: LifeCycleEntry?
) {

    val entries: List<LifeCycleEntry>
        get() = listOfNotNull(screenEntry, dialogEntry, bottomSheetEntry)
}

package com.roudikk.guia.backstack

import com.roudikk.guia.core.BottomSheet
import com.roudikk.guia.core.Dialog
import com.roudikk.guia.core.Navigator
import com.roudikk.guia.core.Screen
import com.roudikk.guia.lifecycle.LifecycleEntry

/**
 * Collection of the currently rendered [LifecycleEntry]s.
 **/
interface RenderGroup {
    val entries: List<LifecycleEntry>
}

/**
 * A [DefaultRenderGroup] will be determined based on the current state of the [Navigator].
 *
 * - [screenEntry] will be the last entry with navigation node being a [Screen], if it exists.
 * - [dialogEntry] will be the last entry if it is a [Dialog], if it exists.
 * - [bottomSheetEntry] could be either the last entry if it's a [BottomSheet] or the entry
 *   right before that if the last entry is a [Dialog]
 *
 * If the current backstack is:
 *  - [..], [Screen], [BottomSheet], [Dialog]
 * Then all entries would be available.
 *
 * If the current backstack is:
 * - [..], [Screen], [Screen], [Dialog], [BottomSheet], [Screen]
 * Then only [screenEntry] will be available
 *
 * If the current backstack is:
 * - [..], [Dialog], [BottomSheet]
 * Then only [bottomSheetEntry] will be available
 *
 * If the current backstack is:
 * - [..], [Screen], [Dialog]
 * Then [screenEntry] and [dialogEntry] will be available
 *
 * If the current backstack is:
 * - [..], [Screen], [BottomSheet]
 * Then [screenEntry] and [bottomSheetEntry] will be available
 *
 * If the current backstack is:
 * - [..], [Screen], [Dialog], [BottomSheet]
 * Then [screenEntry] and [bottomSheetEntry] will be available.
 */
class DefaultRenderGroup(
    val screenEntry: LifecycleEntry? = null,
    val dialogEntry: LifecycleEntry? = null,
    val bottomSheetEntry: LifecycleEntry? = null
) : RenderGroup {

    override val entries: List<LifecycleEntry>
        get() = listOfNotNull(screenEntry, dialogEntry, bottomSheetEntry)
}

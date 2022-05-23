package com.roudikk.navigator.core

import android.os.Parcelable
import com.roudikk.navigator.Navigator
import com.roudikk.navigator.animation.NavEnterExitTransition
import kotlinx.parcelize.Parcelize

/**
 * Navigation state representing the current state of a [Navigator].
 *
 * @property navigationStacks, list of stacks in the current state, either a singleton if
 * navigator is using a [NavigationConfig.SingleStack] or multiple entries when using
 * [NavigationConfig.MultiStack].
 * @property currentStackKey, the current [StackKey].
 * @property transition, the current transition based on the last navigation operation.
 * @property overrideBackPress, whether or not to override back press.
 * @property currentStack, the current stack matching [currentStackKey].
 */
@Parcelize
data class NavigationState(
    val navigationStacks: List<NavigationStack>,
    val currentStackKey: StackKey,
    val transition: NavEnterExitTransition,
    val overrideBackPress: Boolean
) : Parcelable {

    val currentStack: NavigationStack
        get() = navigationStacks.first { it.key == currentStackKey }
}

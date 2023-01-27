package com.roudikk.guia.animation

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition

/**
 * A transition between navigation keys.
 *
 * @property enterExit, is the transition when the navigation key is initially added to the backstack.
 * @property popEnterExit, is the transition when the navigation key is popped to in the backstack.
 */
class NavigationTransition(
    val enterExit: EnterExitTransition,
    val popEnterExit: EnterExitTransition
) {
    companion object {
        // Empty Navigation Transition.
        val None = NavigationTransition(
            enterExit = EnterExitTransition.None,
            popEnterExit = EnterExitTransition.None
        )
    }
}

/**
 * Helper for creating a [NavigationTransition] from two [EnterExitTransition].
 */
infix fun EnterExitTransition.to(
    popEnterExit: EnterExitTransition
) = NavigationTransition(
    enterExit = this,
    popEnterExit = popEnterExit
)

/**
 * A pair of an [EnterTransition] and an [ExitTransition].
 *
 * @property enter, the transition when the navigation key enters.
 * @property exit, the transition when the navigation key exits.
 */
class EnterExitTransition(
    val enter: EnterTransition,
    val exit: ExitTransition
) {
    companion object {
        // Empty transition.
        val None = EnterExitTransition(
            enter = EnterTransition.None,
            exit = ExitTransition.None
        )
    }
}

/**
 * Helper for creating a [EnterExitTransition] from an [EnterTransition] and an [ExitTransition].
 */
infix fun EnterTransition.to(
    exit: ExitTransition
) = EnterExitTransition(
    enter = this,
    exit = exit
)

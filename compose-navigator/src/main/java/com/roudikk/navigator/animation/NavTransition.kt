package com.roudikk.navigator.animation

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Navigation transition for a destination
 *
 * @property enter, used for the new destination when navigating towards it.
 * @property exit, used as the old destination's animation when navigating
 * towards the new destination.
 * @property popEnter, used for the old destination's animation when navigation back to it.
 * @property popExit, used for the current destination's animation when navigation out of it.
 */
@Parcelize
data class NavTransition(
    val enter: NavEnterTransition,
    val exit: NavExitTransition,
    val popEnter: NavEnterTransition,
    val popExit: NavExitTransition
) : Parcelable {

    val enterExit: NavEnterExitTransition
        get() = enter to exit

    val popEnterExit: NavEnterExitTransition
        get() = popEnter to popExit

    companion object {
        val None = NavTransition(
            enter = NavEnterTransition.None,
            exit = NavExitTransition.None,
            popEnter = NavEnterTransition.None,
            popExit = NavExitTransition.None
        )
    }
}

/**
 * Holder for a pair of [NavEnterTransition] and [NavExitTransition].
 */
@Parcelize
data class NavEnterExitTransition(
    val enter: NavEnterTransition,
    val exit: NavExitTransition
) : Parcelable {

    companion object {
        val None = NavEnterExitTransition(
            enter = NavEnterTransition.None,
            exit = NavExitTransition.None
        )
    }
}

/**
 * Convenience function to create a [NavEnterExitTransition].
 */
infix fun NavEnterTransition.to(
    that: NavExitTransition
) = NavEnterExitTransition(this, that)

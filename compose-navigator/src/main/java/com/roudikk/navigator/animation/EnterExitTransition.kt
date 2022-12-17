package com.roudikk.navigator.animation

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition

data class NavigationTransition(
    val enterExit: EnterExitTransition,
    val popEnterExit: EnterExitTransition
) {

    companion object {
        val None = NavigationTransition(
            enterExit = EnterExitTransition.None,
            popEnterExit = EnterExitTransition.None
        )
    }
}

data class EnterExitTransition(
    val enter: EnterTransition,
    val exit: ExitTransition
) {

    companion object {
        val None = EnterExitTransition(
            enter = EnterTransition.None,
            exit = ExitTransition.None
        )
    }
}

infix fun EnterTransition.to(
    exit: ExitTransition
) = EnterExitTransition(
    enter = this,
    exit = exit
)

infix fun EnterExitTransition.to(
    popEnterExit: EnterExitTransition
) = NavigationTransition(
    enterExit = this,
    popEnterExit = popEnterExit
)

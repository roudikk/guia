package com.roudikk.navigator.sample.navigation

import com.roudikk.navigator.animation.NavTransition
import com.roudikk.navigator.animation.navTween
import com.roudikk.navigator.animation.transitions.*

val MaterialSharedAxisTransitionX = NavTransition(
    enter = navSlideInHorizontally { (it * 0.2f).toInt() } +
            navFadeIn(animationSpec = navTween(300)),

    exit = navSlideOutHorizontally { -(it * 0.1f).toInt() } +
            navFadeOut(animationSpec = navTween(150)),

    popEnter = navSlideInHorizontally { -(it * 0.1f).toInt() } +
            navFadeIn(animationSpec = navTween(300)),

    popExit = navSlideOutHorizontally { (it * 0.2f).toInt() } +
            navFadeOut(animationSpec = navTween(150))
)

val MaterialSharedAxisTransitionXY = NavTransition(
    enter = navFadeIn(animationSpec = navTween(300)) +
            navScaleIn(initialScale = 0.8f, animationSpec = navTween(300)),

    exit = navScaleOut(targetScale = 1.1f, animationSpec = navTween(300)) +
            navFadeOut(animationSpec = navTween(durationMillis = 150)),

    popEnter = navFadeIn(animationSpec = navTween(durationMillis = 300)) +
            navScaleIn(initialScale = 1.1f, animationSpec = navTween(300)),

    popExit = navScaleOut(targetScale = 0.8f, animationSpec = navTween(300)) +
            navFadeOut(animationSpec = navTween(durationMillis = 150))
)

val VerticalSlideTransition = NavTransition(
    enter = navSlideInVertically { it / 2 } +
            navFadeIn(),
    exit = navSlideOutVertically { -it / 2 } +
            navFadeOut(),
    popEnter = navSlideInVertically { -it / 2 } +
            navFadeIn(),
    popExit = navSlideOutVertically { it / 2 } +
            navFadeOut()
)

val CrossFadeTransition = NavTransition(
    enter = navFadeIn(),
    exit = navFadeOut(),
    popEnter = navFadeIn(),
    popExit = navFadeOut()
)

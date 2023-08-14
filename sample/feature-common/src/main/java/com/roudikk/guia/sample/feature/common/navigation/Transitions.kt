package com.roudikk.guia.sample.feature.common.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import com.roudikk.guia.animation.NavTransition
import com.roudikk.guia.animation.to

val MaterialSharedAxisTransitionX = NavTransition(
    enterExit = slideInHorizontally { (it * 0.2f).toInt() } + fadeIn(
        animationSpec = tween(300)
    ) to slideOutHorizontally { -(it * 0.1f).toInt() } + fadeOut(
        animationSpec = tween(150)
    ),

    popEnterExit = slideInHorizontally { -(it * 0.1f).toInt() } + fadeIn(
        animationSpec = tween(300)
    ) to slideOutHorizontally { (it * 0.2f).toInt() } + fadeOut(
        animationSpec = tween(150)
    ),
)

val MaterialSharedAxisTransitionXY = NavTransition(
    enterExit = fadeIn(animationSpec = tween(durationMillis = 300)) + scaleIn(
        initialScale = 0.8f,
        animationSpec = tween(durationMillis = 300)
    ) to scaleOut(
        targetScale = 1.1f,
        animationSpec = tween(durationMillis = 300)
    ) + fadeOut(animationSpec = tween(durationMillis = 150)),

    popEnterExit = fadeIn(animationSpec = tween(durationMillis = 300)) + scaleIn(
        initialScale = 1.1f,
        animationSpec = tween(durationMillis = 300)
    ) to scaleOut(
        targetScale = 0.8f,
        animationSpec = tween(durationMillis = 300)
    ) + fadeOut(animationSpec = tween(durationMillis = 150))
)

val VerticalSlideTransition = NavTransition(
    enterExit = slideInVertically { it / 2 } + fadeIn() to slideOutVertically {
        -it / 2
    } + fadeOut(),
    popEnterExit = slideInVertically { -it / 2 } + fadeIn() to slideOutVertically {
        it / 2
    } + fadeOut()
)

val CrossFadeTransition = NavTransition(
    enterExit = fadeIn() to fadeOut(),
    popEnterExit = fadeIn() to fadeOut()
)

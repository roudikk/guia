@file:Suppress("unused")

package com.roudikk.navigator.animation.transitions

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.VisibilityThreshold
import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import com.roudikk.navigator.animation.NavAlignment
import com.roudikk.navigator.animation.NavChangeSize
import com.roudikk.navigator.animation.NavExitTransition
import com.roudikk.navigator.animation.NavFade
import com.roudikk.navigator.animation.NavFiniteAnimationSpec
import com.roudikk.navigator.animation.NavIntOffset
import com.roudikk.navigator.animation.NavIntSize
import com.roudikk.navigator.animation.NavScale
import com.roudikk.navigator.animation.NavSlide
import com.roudikk.navigator.animation.NavTransitionData
import com.roudikk.navigator.animation.navSpring
import com.roudikk.navigator.animation.toAlignment
import com.roudikk.navigator.animation.toNavTransformOrigin
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.animation.shrinkOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideOut
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically

/**
 * One to one match of [fadeOut]
 */
@Stable
fun navFadeOut(
    animationSpec: NavFiniteAnimationSpec<Float> = navSpring(stiffness = Spring.StiffnessMediumLow),
    targetAlpha: Float = 0f,
): NavExitTransition {
    return NavExitTransition(
        NavTransitionData(
            fade = NavFade(
                targetAlpha,
                animationSpec
            )
        )
    )
}

/**
 * One to one match of [slideOut]
 */
@Stable
fun navSlideOut(
    animationSpec: NavFiniteAnimationSpec<IntOffset> =
        navSpring(
            stiffness = Spring.StiffnessMediumLow,
            visibilityThreshold = IntOffset.VisibilityThreshold
        ),
    targetOffset: (fullSize: NavIntSize) -> NavIntOffset,
): NavExitTransition {
    return NavExitTransition(
        NavTransitionData(
            slide = NavSlide(
                targetOffset,
                animationSpec
            )
        )
    )
}

/**
 * One to one match of [scaleOut]
 */
@Stable
fun navScaleOut(
    animationSpec: NavFiniteAnimationSpec<Float> = navSpring(stiffness = Spring.StiffnessMediumLow),
    targetScale: Float = 0f,
    transformOrigin: TransformOrigin = TransformOrigin.Center
): NavExitTransition {
    return NavExitTransition(
        NavTransitionData(
            scale = NavScale(
                targetScale,
                transformOrigin.toNavTransformOrigin(), animationSpec
            )
        )
    )
}

/**
 * One to one match of [shrinkOut]
 */
@Stable
fun navShrinkOut(
    animationSpec: NavFiniteAnimationSpec<IntSize> =
        navSpring(
            stiffness = Spring.StiffnessMediumLow,
            visibilityThreshold = IntSize.VisibilityThreshold
        ),
    shrinkTowards: NavAlignment = NavAlignment.BottomEnd,
    clip: Boolean = true,
    targetSize: (fullSize: NavIntSize) -> NavIntSize = {
        NavIntSize(
            0,
            0
        )
    },
): NavExitTransition {
    return NavExitTransition(
        NavTransitionData(
            changeSize = NavChangeSize(
                shrinkTowards,
                targetSize,
                animationSpec,
                clip
            )
        )
    )
}

/**
 * One to one match of [slideOutHorizontally]
 */
@Stable
fun navSlideOutHorizontally(
    animationSpec: NavFiniteAnimationSpec<IntOffset> =
        navSpring(
            stiffness = Spring.StiffnessMediumLow,
            visibilityThreshold = IntOffset.VisibilityThreshold
        ),
    targetOffsetX: (fullWidth: Int) -> Int = { -it / 2 },
): NavExitTransition = navSlideOut(
    targetOffset = {
        NavIntOffset(
            targetOffsetX(
                it.width
            ), 0
        )
    },
    animationSpec = animationSpec
)

/**
 * One to one match of [slideOutVertically]
 */
@Stable
fun navSlideOutVertically(
    animationSpec: NavFiniteAnimationSpec<IntOffset> =
        navSpring(
            stiffness = Spring.StiffnessMediumLow,
            visibilityThreshold = IntOffset.VisibilityThreshold
        ),
    targetOffsetY: (fullHeight: Int) -> Int = { -it / 2 },
): NavExitTransition = navSlideOut(
    targetOffset = {
        NavIntOffset(
            0,
            targetOffsetY(it.height)
        )
    },
    animationSpec = animationSpec
)

/**
 * One to one match of [shrinkHorizontally]
 */
@Stable
fun navShrinkHorizontally(
    animationSpec: NavFiniteAnimationSpec<IntSize> =
        navSpring(
            stiffness = Spring.StiffnessMediumLow,
            visibilityThreshold = IntSize.VisibilityThreshold
        ),
    shrinkTowards: NavAlignment.Horizontal = NavAlignment.End,
    clip: Boolean = true,
    targetWidth: (fullWidth: Int) -> Int = { 0 }
): NavExitTransition =
    navShrinkOut(animationSpec, shrinkTowards.toAlignment(), clip) {
        NavIntSize(
            targetWidth(it.width),
            it.height
        )
    }

/**
 * One to one match of [shrinkVertically]
 */
@Stable
fun navShrinkVertically(
    animationSpec: NavFiniteAnimationSpec<IntSize> =
        navSpring(
            stiffness = Spring.StiffnessMediumLow,
            visibilityThreshold = IntSize.VisibilityThreshold
        ),
    shrinkTowards: NavAlignment.Vertical = NavAlignment.Bottom,
    clip: Boolean = true,
    targetHeight: (fullHeight: Int) -> Int = { 0 },
): NavExitTransition =
    navShrinkOut(animationSpec, shrinkTowards.toAlignment(), clip) {
        NavIntSize(
            it.width,
            targetHeight(it.height)
        )
    }

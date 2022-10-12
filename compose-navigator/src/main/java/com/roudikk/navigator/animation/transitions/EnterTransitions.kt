@file:Suppress("unused")

package com.roudikk.navigator.animation.transitions

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.VisibilityThreshold
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.expandIn
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideIn
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import com.roudikk.navigator.animation.NavAlignment
import com.roudikk.navigator.animation.NavChangeSize
import com.roudikk.navigator.animation.NavEnterTransition
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

/**
 * One to one match of [fadeIn]
 */
@Stable
fun navFadeIn(
    animationSpec: NavFiniteAnimationSpec<Float> = navSpring(stiffness = Spring.StiffnessMediumLow),
    initialAlpha: Float = 0f
): NavEnterTransition {
    return NavEnterTransition(
        NavTransitionData(
            fade = NavFade(
                initialAlpha,
                animationSpec
            )
        )
    )
}

/**
 * One to one match of [slideIn]
 */
@Stable
fun navSlideIn(
    animationSpec: NavFiniteAnimationSpec<IntOffset> =
        navSpring(
            stiffness = Spring.StiffnessMediumLow,
            visibilityThreshold = IntOffset.VisibilityThreshold
        ),
    initialOffset: (fullSize: NavIntSize) -> NavIntOffset,
): NavEnterTransition {
    return NavEnterTransition(
        NavTransitionData(
            slide = NavSlide(
                initialOffset,
                animationSpec
            )
        )
    )
}

/**
 * One to one match of [scaleIn]
 */
@Stable
fun navScaleIn(
    animationSpec: NavFiniteAnimationSpec<Float> = navSpring(stiffness = Spring.StiffnessMediumLow),
    initialScale: Float = 0f,
    transformOrigin: TransformOrigin = TransformOrigin.Center,
): NavEnterTransition {
    return NavEnterTransition(
        NavTransitionData(
            scale = NavScale(
                initialScale,
                transformOrigin.toNavTransformOrigin(),
                animationSpec
            )
        )
    )
}

/**
 * One to one match of [expandIn]
 */
@Stable
fun navExpandIn(
    animationSpec: NavFiniteAnimationSpec<IntSize> =
        navSpring(
            stiffness = Spring.StiffnessMediumLow,
            visibilityThreshold = IntSize.VisibilityThreshold
        ),
    expandFrom: NavAlignment = NavAlignment.BottomEnd,
    clip: Boolean = true,
    initialSize: (fullSize: NavIntSize) -> NavIntSize = {
        NavIntSize(
            0,
            0
        )
    },
): NavEnterTransition {
    return NavEnterTransition(
        NavTransitionData(
            changeSize = NavChangeSize(
                expandFrom,
                initialSize,
                animationSpec,
                clip
            )
        )
    )
}

/**
 * One to one match of [expandHorizontally]
 */
@Stable
fun navExpandHorizontally(
    animationSpec: NavFiniteAnimationSpec<IntSize> =
        navSpring(
            stiffness = Spring.StiffnessMediumLow,
            visibilityThreshold = IntSize.VisibilityThreshold
        ),
    expandFrom: NavAlignment.Horizontal = NavAlignment.End,
    clip: Boolean = true,
    initialWidth: (fullWidth: Int) -> Int = { 0 },
): NavEnterTransition =
    navExpandIn(animationSpec, expandFrom.toAlignment(), clip = clip) {
        NavIntSize(
            initialWidth(it.width),
            it.height
        )
    }

/**
 * One to one match of [expandVertically]
 */
@Stable
fun navExpandVertically(
    animationSpec: NavFiniteAnimationSpec<IntSize> =
        navSpring(
            stiffness = Spring.StiffnessMediumLow,
            visibilityThreshold = IntSize.VisibilityThreshold
        ),
    expandFrom: NavAlignment.Vertical = NavAlignment.Bottom,
    clip: Boolean = true,
    initialHeight: (fullHeight: Int) -> Int = { 0 },
): NavEnterTransition = navExpandIn(animationSpec, expandFrom.toAlignment(), clip) {
    NavIntSize(it.width, initialHeight(it.height))
}

/**
 * One to one match of [slideInHorizontally]
 */
@Stable
fun navSlideInHorizontally(
    animationSpec: NavFiniteAnimationSpec<IntOffset> =
        navSpring(
            stiffness = Spring.StiffnessMediumLow,
            visibilityThreshold = IntOffset.VisibilityThreshold
        ),
    initialOffsetX: (fullWidth: Int) -> Int = { -it / 2 },
): NavEnterTransition = navSlideIn(
    initialOffset = {
        NavIntOffset(initialOffsetX(it.width), 0)
    },
    animationSpec = animationSpec
)

/**
 * One to one match of [slideInVertically]
 */
@Stable
fun navSlideInVertically(
    animationSpec: NavFiniteAnimationSpec<IntOffset> =
        navSpring(
            stiffness = Spring.StiffnessMediumLow,
            visibilityThreshold = IntOffset.VisibilityThreshold
        ),
    initialOffsetY: (fullHeight: Int) -> Int = { -it / 2 },
): NavEnterTransition = navSlideIn(
    initialOffset = {
        NavIntOffset(0, initialOffsetY(it.height))
    },
    animationSpec = animationSpec
)

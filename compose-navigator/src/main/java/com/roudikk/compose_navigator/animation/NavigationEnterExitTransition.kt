package com.roudikk.compose_navigator.animation

import android.os.Parcelable
import androidx.compose.animation.*
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.VisibilityThreshold
import androidx.compose.runtime.Stable
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import kotlinx.parcelize.Parcelize

/**
 * [EnterTransition] and [ExitTransition] are not savable in a bundle and cannot be saved/restored
 * when the state of the app is saved/restored. And they are sealed and final so there is no easy
 * way to extend them and make them savable.
 *
 * This is a one to one match of all the [EnterTransition] and [ExitTransition] defined.
 * Prepend "navigation" to the compose equivalent function to find the navigation version of it.
 * For ex: fadeIn() -> navigationFadeIn()
 *
 * [EnterTransition] is converted to [NavigationEnterTransition]
 * [ExitTransition] is converted to [NavigationExitTransition]
 */

@Parcelize
data class NavigationEnterTransition(
    val data: NavigationTransitionData
) : Parcelable {

    operator fun plus(enter: NavigationEnterTransition): NavigationEnterTransition {
        return NavigationEnterTransition(
            NavigationTransitionData(
                fade = data.fade ?: enter.data.fade,
                slide = data.slide ?: enter.data.slide,
                changeSize = data.changeSize ?: enter.data.changeSize,
                scale = data.scale ?: enter.data.scale
            )
        )
    }

    @OptIn(ExperimentalAnimationApi::class)
    fun toComposeEnterTransition(): EnterTransition {
        val transitions = mutableListOf<EnterTransition>()

        if (data.fade != null) {
            transitions.add(
                fadeIn(
                    initialAlpha = data.fade.alpha,
                    animationSpec = data.fade.animationSpec.toComposeSpec()
                )
            )
        }

        if (data.slide != null) {
            transitions.add(
                slideIn(
                    initialOffset = data.slide.composeSlideOffset(),
                    animationSpec = data.slide.animationSpec.toComposeSpec()
                )
            )
        }

        if (data.changeSize != null) {
            transitions.add(
                expandIn(
                    expandFrom = data.changeSize.alignment.toComposeAlignment(),
                    clip = data.changeSize.clip,
                    initialSize = {
                        data.changeSize.size(
                            NavigationIntSize.fromComposeIntSize(it)
                        ).toComposeIntSize()
                    },
                    animationSpec = data.changeSize.animationSpec.toComposeSpec()
                )
            )
        }

        if (data.scale != null) {
            transitions.add(
                scaleIn(
                    initialScale = data.scale.scale,
                    transformOrigin = data.scale.transformOrigin.toComposeTransformOrigin(),
                    animationSpec = data.scale.animationSpec.toComposeSpec()
                )
            )
        }

        var enterTransition = transitions.getOrNull(0)

        return if (enterTransition != null) {
            transitions.forEachIndexed { index, transition ->
                if (index != 0) {
                    enterTransition += transition
                }
            }

            enterTransition
        } else {
            EnterTransition.None
        }
    }

    companion object {
        val None: NavigationEnterTransition = NavigationEnterTransition(NavigationTransitionData())
    }
}

@Parcelize
data class NavigationExitTransition(
    val data: NavigationTransitionData
) : Parcelable {

    operator fun plus(enter: NavigationExitTransition): NavigationExitTransition {
        return NavigationExitTransition(
            NavigationTransitionData(
                fade = data.fade ?: enter.data.fade,
                slide = data.slide ?: enter.data.slide,
                changeSize = data.changeSize ?: enter.data.changeSize,
                scale = data.scale ?: enter.data.scale
            )
        )
    }

    @OptIn(ExperimentalAnimationApi::class)
    fun toComposeExitTransition(): ExitTransition {
        val transitions = mutableListOf<ExitTransition>()

        if (data.fade != null) {
            transitions.add(
                fadeOut(
                    targetAlpha = data.fade.alpha,
                    animationSpec = data.fade.animationSpec.toComposeSpec()
                )
            )
        }

        if (data.slide != null) {
            transitions.add(
                slideOut(
                    targetOffset = {
                        data.slide.slideOffset(
                            NavigationIntSize.fromComposeIntSize(it)
                        ).toComposeIntOffset()
                    },
                    animationSpec = data.slide.animationSpec.toComposeSpec()
                )
            )
        }

        if (data.changeSize != null) {
            transitions.add(
                shrinkOut(
                    shrinkTowards = data.changeSize.alignment.toComposeAlignment(),
                    clip = data.changeSize.clip,
                    targetSize = {
                        data.changeSize.size(
                            NavigationIntSize.fromComposeIntSize(it)
                        ).toComposeIntSize()
                    },
                    animationSpec = data.changeSize.animationSpec.toComposeSpec()
                )
            )
        }

        if (data.scale != null) {
            transitions.add(
                scaleOut(
                    targetScale = data.scale.scale,
                    transformOrigin = data.scale.transformOrigin.toComposeTransformOrigin(),
                    animationSpec = data.scale.animationSpec.toComposeSpec()
                )
            )
        }

        var exitTransition = transitions.getOrNull(0)

        return if (exitTransition != null) {
            transitions.forEachIndexed { index, transition ->
                if (index != 0) {
                    exitTransition += transition
                }
            }
            exitTransition
        } else {
            ExitTransition.None
        }
    }


    companion object {
        val None: NavigationExitTransition = NavigationExitTransition(NavigationTransitionData())
    }
}

@Parcelize
data class NavigationTransitionData(
    val fade: NavigationFade? = null,
    val slide: NavigationSlide? = null,
    val changeSize: NavigationChangeSize? = null,
    val scale: NavigationScale? = null
) : Parcelable

@Parcelize
data class NavigationFade(
    val alpha: Float,
    val animationSpec: NavigationFiniteAnimationSpec<Float>
) : Parcelable


@Parcelize
data class NavigationSlide(
    val slideOffset: (fullSize: NavigationIntSize) -> NavigationIntOffset,
    val animationSpec: NavigationFiniteAnimationSpec<IntOffset>
) : Parcelable {

    fun composeSlideOffset(): ((fullSize: IntSize) -> IntOffset) = {
        slideOffset(
            NavigationIntSize.fromComposeIntSize(it)
        ).toComposeIntOffset()
    }
}

@Parcelize
data class NavigationChangeSize(
    val alignment: NavigationAlignment,
    val size: (fullSize: NavigationIntSize) -> NavigationIntSize = { NavigationIntSize(0, 0) },
    val animationSpec: NavigationFiniteAnimationSpec<IntSize>,
    val clip: Boolean = true
) : Parcelable

@Parcelize
data class NavigationScale(
    val scale: Float,
    val transformOrigin: NavigationTransformOrigin,
    val animationSpec: NavigationFiniteAnimationSpec<Float>
) : Parcelable

@Parcelize
data class NavigationIntSize(
    val width: Int,
    val height: Int
) : Parcelable {

    companion object {
        fun fromComposeIntSize(intSize: IntSize) = NavigationIntSize(intSize.width, intSize.height)
    }

    fun toComposeIntSize() = IntSize(width, height)
}

@Parcelize
data class NavigationIntOffset(
    val x: Int,
    val y: Int
) : Parcelable {

    companion object {
        fun fromComposeIntOffset(intOffset: IntOffset) =
            NavigationIntOffset(intOffset.x, intOffset.y)
    }

    fun toComposeIntOffset() = IntOffset(x, y)
}

@Parcelize
data class NavigationTransformOrigin(
    val pivotFractionX: Float,
    val pivotFractionY: Float
) : Parcelable {

    fun toComposeTransformOrigin() = TransformOrigin(pivotFractionX, pivotFractionY)
}

fun TransformOrigin.toNavigationTransformOrigin() =
    NavigationTransformOrigin(pivotFractionX, pivotFractionY)


sealed class NavigationAlignment(
    private val horizontalBias: Float,
    private val verticalBias: Float
) : Parcelable {

    @Parcelize
    object TopStart : NavigationAlignment(-1f, -1f)

    @Parcelize
    object TopCenter : NavigationAlignment(0f, -1f)

    @Parcelize
    object TopEnd : NavigationAlignment(1f, -1f)

    @Parcelize
    object CenterStart : NavigationAlignment(-1f, 0f)

    @Parcelize
    object Center : NavigationAlignment(0f, 0f)

    @Parcelize
    object CenterEnd : NavigationAlignment(1f, 0f)

    @Parcelize
    object BottomStart : NavigationAlignment(-1f, 1f)

    @Parcelize
    object BottomCenter : NavigationAlignment(0f, 1f)

    @Parcelize
    object BottomEnd : NavigationAlignment(1f, 1f)

    open class Vertical

    object Top : Vertical()
    object CenterVertically : Vertical()
    object Bottom : Vertical()

    open class Horizontal

    object Start : Horizontal()
    object CenterHorizontally : Horizontal()
    object End : Horizontal()

    fun toComposeAlignment() = BiasAlignment(horizontalBias, verticalBias)
}

private fun NavigationAlignment.Vertical.toAlignment() =
    when (this) {
        NavigationAlignment.Top -> NavigationAlignment.TopCenter
        NavigationAlignment.Bottom -> NavigationAlignment.BottomCenter
        else -> NavigationAlignment.Center
    }

private fun NavigationAlignment.Horizontal.toAlignment() =
    when (this) {
        NavigationAlignment.Start -> NavigationAlignment.CenterStart
        NavigationAlignment.End -> NavigationAlignment.CenterEnd
        else -> NavigationAlignment.Center
    }

/**
 * @see [fadeIn]
 */
@Stable
fun navigationFadeIn(
    animationSpec: NavigationFiniteAnimationSpec<Float> = navigationSpring(stiffness = Spring.StiffnessMediumLow),
    initialAlpha: Float = 0f
): NavigationEnterTransition {
    return NavigationEnterTransition(
        NavigationTransitionData(
            fade = NavigationFade(
                initialAlpha,
                animationSpec
            )
        )
    )
}

/**
 * @see [fadeOut]
 */
@Stable
fun navigationFadeOut(
    animationSpec: NavigationFiniteAnimationSpec<Float> = navigationSpring(stiffness = Spring.StiffnessMediumLow),
    targetAlpha: Float = 0f,
): NavigationExitTransition {
    return NavigationExitTransition(
        NavigationTransitionData(
            fade = NavigationFade(
                targetAlpha,
                animationSpec
            )
        )
    )
}

/**
 * @see [slideIn]
 */
@Stable
fun navigationSlideIn(
    animationSpec: NavigationFiniteAnimationSpec<IntOffset> =
        navigationSpring(
            stiffness = Spring.StiffnessMediumLow,
            visibilityThreshold = IntOffset.VisibilityThreshold
        ),
    initialOffset: (fullSize: NavigationIntSize) -> NavigationIntOffset,
): NavigationEnterTransition {
    return NavigationEnterTransition(
        NavigationTransitionData(
            slide = NavigationSlide(
                initialOffset,
                animationSpec
            )
        )
    )
}

/**
 * @see [slideOut]
 */
@Stable
fun navigationSlideOut(
    animationSpec: NavigationFiniteAnimationSpec<IntOffset> =
        navigationSpring(
            stiffness = Spring.StiffnessMediumLow,
            visibilityThreshold = IntOffset.VisibilityThreshold
        ),
    targetOffset: (fullSize: NavigationIntSize) -> NavigationIntOffset,
): NavigationExitTransition {
    return NavigationExitTransition(
        NavigationTransitionData(
            slide = NavigationSlide(
                targetOffset,
                animationSpec
            )
        )
    )
}

/**
 * @see [scaleIn]
 */
@Stable
@ExperimentalAnimationApi
fun navigationScaleIn(
    animationSpec: NavigationFiniteAnimationSpec<Float> = navigationSpring(stiffness = Spring.StiffnessMediumLow),
    initialScale: Float = 0f,
    transformOrigin: TransformOrigin = TransformOrigin.Center,
): NavigationEnterTransition {
    return NavigationEnterTransition(
        NavigationTransitionData(
            scale = NavigationScale(
                initialScale,
                transformOrigin.toNavigationTransformOrigin(),
                animationSpec
            )
        )
    )
}

/**
 * @see [scaleOut]
 */
@Stable
@ExperimentalAnimationApi
fun navigationScaleOut(
    animationSpec: NavigationFiniteAnimationSpec<Float> = navigationSpring(stiffness = Spring.StiffnessMediumLow),
    targetScale: Float = 0f,
    transformOrigin: TransformOrigin = TransformOrigin.Center
): NavigationExitTransition {
    return NavigationExitTransition(
        NavigationTransitionData(
            scale = NavigationScale(
                targetScale,
                transformOrigin.toNavigationTransformOrigin(), animationSpec
            )
        )
    )
}

/**
 * @see [expandIn]
 */
@Stable
fun navigationExpandIn(
    animationSpec: NavigationFiniteAnimationSpec<IntSize> =
        navigationSpring(
            stiffness = Spring.StiffnessMediumLow,
            visibilityThreshold = IntSize.VisibilityThreshold
        ),
    expandFrom: NavigationAlignment = NavigationAlignment.BottomEnd,
    clip: Boolean = true,
    initialSize: (fullSize: NavigationIntSize) -> NavigationIntSize = { NavigationIntSize(0, 0) },
): NavigationEnterTransition {
    return NavigationEnterTransition(
        NavigationTransitionData(
            changeSize = NavigationChangeSize(
                expandFrom,
                initialSize,
                animationSpec,
                clip
            )
        )
    )
}

/**
 * @see [shrinkOut]
 */
@Stable
fun navigationShrinkOut(
    animationSpec: NavigationFiniteAnimationSpec<IntSize> =
        navigationSpring(
            stiffness = Spring.StiffnessMediumLow,
            visibilityThreshold = IntSize.VisibilityThreshold
        ),
    shrinkTowards: NavigationAlignment = NavigationAlignment.BottomEnd,
    clip: Boolean = true,
    targetSize: (fullSize: NavigationIntSize) -> NavigationIntSize = { NavigationIntSize(0, 0) },
): NavigationExitTransition {
    return NavigationExitTransition(
        NavigationTransitionData(
            changeSize = NavigationChangeSize(
                shrinkTowards,
                targetSize,
                animationSpec,
                clip
            )
        )
    )
}

/**
 * @see [expandHorizontally]
 */
@Stable
fun navigationExpandHorizontally(
    animationSpec: NavigationFiniteAnimationSpec<IntSize> =
        navigationSpring(
            stiffness = Spring.StiffnessMediumLow,
            visibilityThreshold = IntSize.VisibilityThreshold
        ),
    expandFrom: NavigationAlignment.Horizontal = NavigationAlignment.End,
    clip: Boolean = true,
    initialWidth: (fullWidth: Int) -> Int = { 0 },
): NavigationEnterTransition =
    navigationExpandIn(animationSpec, expandFrom.toAlignment(), clip = clip) {
        NavigationIntSize(initialWidth(it.width), it.height)
    }

/**
 * @see [expandVertically]
 */
@Stable
fun navigationExpandVertically(
    animationSpec: NavigationFiniteAnimationSpec<IntSize> =
        navigationSpring(
            stiffness = Spring.StiffnessMediumLow,
            visibilityThreshold = IntSize.VisibilityThreshold
        ),
    expandFrom: NavigationAlignment.Vertical = NavigationAlignment.Bottom,
    clip: Boolean = true,
    initialHeight: (fullHeight: Int) -> Int = { 0 },
): NavigationEnterTransition = navigationExpandIn(animationSpec, expandFrom.toAlignment(), clip) {
    NavigationIntSize(it.width, initialHeight(it.height))
}

/**
 * @see [shrinkHorizontally]
 */
@Stable
fun navigationShrinkHorizontally(
    animationSpec: NavigationFiniteAnimationSpec<IntSize> =
        navigationSpring(
            stiffness = Spring.StiffnessMediumLow,
            visibilityThreshold = IntSize.VisibilityThreshold
        ),
    shrinkTowards: NavigationAlignment.Horizontal = NavigationAlignment.End,
    clip: Boolean = true,
    targetWidth: (fullWidth: Int) -> Int = { 0 }
): NavigationExitTransition =
    navigationShrinkOut(animationSpec, shrinkTowards.toAlignment(), clip) {
        NavigationIntSize(targetWidth(it.width), it.height)
    }

/**
 * @see [shrinkVertically]
 */
@Stable
fun navigationShrinkVertically(
    animationSpec: NavigationFiniteAnimationSpec<IntSize> =
        navigationSpring(
            stiffness = Spring.StiffnessMediumLow,
            visibilityThreshold = IntSize.VisibilityThreshold
        ),
    shrinkTowards: NavigationAlignment.Vertical = NavigationAlignment.Bottom,
    clip: Boolean = true,
    targetHeight: (fullHeight: Int) -> Int = { 0 },
): NavigationExitTransition =
    navigationShrinkOut(animationSpec, shrinkTowards.toAlignment(), clip) {
        NavigationIntSize(it.width, targetHeight(it.height))
    }

/**
 * @see [slideInHorizontally]
 */
@Stable
fun navigationSlideInHorizontally(
    animationSpec: NavigationFiniteAnimationSpec<IntOffset> =
        navigationSpring(
            stiffness = Spring.StiffnessMediumLow,
            visibilityThreshold = IntOffset.VisibilityThreshold
        ),
    initialOffsetX: (fullWidth: Int) -> Int = { -it / 2 },
): NavigationEnterTransition = navigationSlideIn(
    initialOffset = { NavigationIntOffset(initialOffsetX(it.width), 0) },
    animationSpec = animationSpec
)

/**
 * @see [slideInVertically]
 */
@Stable
fun navigationSlideInVertically(
    animationSpec: NavigationFiniteAnimationSpec<IntOffset> =
        navigationSpring(
            stiffness = Spring.StiffnessMediumLow,
            visibilityThreshold = IntOffset.VisibilityThreshold
        ),
    initialOffsetY: (fullHeight: Int) -> Int = { -it / 2 },
): NavigationEnterTransition = navigationSlideIn(
    initialOffset = { NavigationIntOffset(0, initialOffsetY(it.height)) },
    animationSpec = animationSpec
)

/**
 * @see [slideOutHorizontally]
 */
@Stable
fun navigationSlideOutHorizontally(
    animationSpec: NavigationFiniteAnimationSpec<IntOffset> =
        navigationSpring(
            stiffness = Spring.StiffnessMediumLow,
            visibilityThreshold = IntOffset.VisibilityThreshold
        ),
    targetOffsetX: (fullWidth: Int) -> Int = { -it / 2 },
): NavigationExitTransition = navigationSlideOut(
    targetOffset = { NavigationIntOffset(targetOffsetX(it.width), 0) },
    animationSpec = animationSpec
)

/**
 * @see [slideOutVertically]
 */
@Stable
fun navigationSlideOutVertically(
    animationSpec: NavigationFiniteAnimationSpec<IntOffset> =
        navigationSpring(
            stiffness = Spring.StiffnessMediumLow,
            visibilityThreshold = IntOffset.VisibilityThreshold
        ),
    targetOffsetY: (fullHeight: Int) -> Int = { -it / 2 },
): NavigationExitTransition = navigationSlideOut(
    targetOffset = { NavigationIntOffset(0, targetOffsetY(it.height)) },
    animationSpec = animationSpec
)

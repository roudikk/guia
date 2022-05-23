@file:Suppress("unused")

package com.roudikk.navigator.animation

import android.os.Parcelable
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import kotlinx.parcelize.Parcelize

/**
 * One to one match of [EnterTransition] and [ExitTransition]'s TransitionData but [Parcelable]
 * so it can be saved and restored. This hold the different animations that the transition contains:
 * Fade, slide, size change and scale.
 */
@Parcelize
internal data class NavTransitionData(
    val fade: NavFade? = null,
    val slide: NavSlide? = null,
    val changeSize: NavChangeSize? = null,
    val scale: NavScale? = null
) : Parcelable

@Parcelize
internal data class NavFade(
    val alpha: Float,
    val animationSpec: NavFiniteAnimationSpec<Float>
) : Parcelable

@Parcelize
internal data class NavSlide(
    val slideOffset: (fullSize: NavIntSize) -> NavIntOffset,
    val animationSpec: NavFiniteAnimationSpec<IntOffset>
) : Parcelable {

    fun composeSlideOffset(): ((fullSize: IntSize) -> IntOffset) = {
        slideOffset(
            NavIntSize.fromComposeIntSize(it)
        ).toComposeIntOffset()
    }
}

@Parcelize
internal data class NavChangeSize(
    val alignment: NavAlignment,
    val size: (fullSize: NavIntSize) -> NavIntSize = { NavIntSize(0, 0) },
    val animationSpec: NavFiniteAnimationSpec<IntSize>,
    val clip: Boolean = true
) : Parcelable

@Parcelize
internal data class NavScale(
    val scale: Float,
    val transformOrigin: NavTransformOrigin,
    val animationSpec: NavFiniteAnimationSpec<Float>
) : Parcelable

@Parcelize
data class NavIntSize(
    val width: Int,
    val height: Int
) : Parcelable {

    companion object {
        fun fromComposeIntSize(intSize: IntSize) = NavIntSize(intSize.width, intSize.height)
    }

    fun toComposeIntSize() = IntSize(width, height)
}

@Parcelize
data class NavIntOffset(
    val x: Int,
    val y: Int
) : Parcelable {

    companion object {
        fun fromComposeIntOffset(intOffset: IntOffset) =
            NavIntOffset(intOffset.x, intOffset.y)
    }

    fun toComposeIntOffset() = IntOffset(x, y)
}

@Parcelize
internal data class NavTransformOrigin(
    val pivotFractionX: Float,
    val pivotFractionY: Float
) : Parcelable {

    fun toComposeTransformOrigin() = TransformOrigin(pivotFractionX, pivotFractionY)
}

internal fun TransformOrigin.toNavTransformOrigin() =
    NavTransformOrigin(pivotFractionX, pivotFractionY)

sealed class NavAlignment(
    private val horizontalBias: Float,
    private val verticalBias: Float
) : Parcelable {

    @Parcelize
    object TopStart : NavAlignment(-1f, -1f)

    @Parcelize
    object TopCenter : NavAlignment(0f, -1f)

    @Parcelize
    object TopEnd : NavAlignment(1f, -1f)

    @Parcelize
    object CenterStart : NavAlignment(-1f, 0f)

    @Parcelize
    object Center : NavAlignment(0f, 0f)

    @Parcelize
    object CenterEnd : NavAlignment(1f, 0f)

    @Parcelize
    object BottomStart : NavAlignment(-1f, 1f)

    @Parcelize
    object BottomCenter : NavAlignment(0f, 1f)

    @Parcelize
    object BottomEnd : NavAlignment(1f, 1f)

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

internal fun NavAlignment.Vertical.toAlignment() =
    when (this) {
        NavAlignment.Top -> NavAlignment.TopCenter
        NavAlignment.Bottom -> NavAlignment.BottomCenter
        else -> NavAlignment.Center
    }

internal fun NavAlignment.Horizontal.toAlignment() =
    when (this) {
        NavAlignment.Start -> NavAlignment.CenterStart
        NavAlignment.End -> NavAlignment.CenterEnd
        else -> NavAlignment.Center
    }

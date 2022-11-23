@file:Suppress("unused")

package com.roudikk.navigator.animation

import android.os.Parcelable
import androidx.compose.animation.core.AnimationConstants
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.SnapSpec
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.SpringSpec
import androidx.compose.animation.core.TweenSpec
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import kotlinx.parcelize.Parcelize

/**
 * One to one match of Compose [AnimationSpec] but [Parcelable] so it can be saved and restored.
 *
 * Currently the supported animation specs are: tween, spring and snap.
 *
 * [TweenSpec] is converted to [NavTweenSpec]
 * [SpringSpec] is converted to [NavSpringSpec]
 * [SnapSpec] is converted to [NavSnapSpec]
 */
sealed interface NavFiniteAnimationSpec<T> : Parcelable {

    fun toComposeSpec(): FiniteAnimationSpec<T>
}

@Parcelize
@JvmInline
value class SpecFloat(val value: Float) : Parcelable

@Parcelize
data class NavSpringSpec<T : Parcelable>(
    private val dampingRatio: Float = Spring.DampingRatioNoBouncy,
    private val stiffness: Float = Spring.StiffnessMedium,
    private val visibilityThreshold: T? = null
) : NavFiniteAnimationSpec<T> {

    @Suppress("UNCHECKED_CAST")
    override fun toComposeSpec(): FiniteAnimationSpec<T> {
        return SpringSpec(
            dampingRatio = dampingRatio,
            stiffness = stiffness,
            visibilityThreshold = when (visibilityThreshold) {
                is SpecFloat -> visibilityThreshold.value
                is NavIntOffset -> visibilityThreshold.toComposeIntOffset()
                is NavIntSize -> visibilityThreshold.toComposeIntSize()
                else -> null
            }
        ) as FiniteAnimationSpec<T>
    }
}

@Parcelize
data class NavTweenSpec<T>(
    val durationMillis: Int,
    val delay: Int,
    val easing: NavEasing
) : NavFiniteAnimationSpec<T> {

    override fun toComposeSpec(): FiniteAnimationSpec<T> {
        return TweenSpec(
            durationMillis = durationMillis,
            delay = delay,
            easing = easing.toComposeEasing()
        )
    }
}

@Parcelize
data class NavSnapSpec<T>(
    val delayMillis: Int
) : NavFiniteAnimationSpec<T> {

    override fun toComposeSpec(): FiniteAnimationSpec<T> {
        return SnapSpec(delayMillis)
    }
}

@Parcelize
enum class NavEasing : Parcelable {
    FastOutSlowIn,
    LinearOutSlowIn,
    FastOutLinearIn,
    Linear;

    fun toComposeEasing() = when (this) {
        FastOutSlowIn -> FastOutSlowInEasing
        LinearOutSlowIn -> LinearOutSlowInEasing
        FastOutLinearIn -> FastOutLinearInEasing
        Linear -> LinearEasing
    }
}

@Suppress("UNCHECKED_CAST")
inline fun <reified T> navSpring(
    dampingRatio: Float = Spring.DampingRatioNoBouncy,
    stiffness: Float = Spring.StiffnessMedium,
    visibilityThreshold: T? = null
): NavFiniteAnimationSpec<T> {
    return NavSpringSpec(
        dampingRatio = dampingRatio,
        stiffness = stiffness,
        visibilityThreshold = visibilityThreshold?.let {
            when (it) {
                is Float -> SpecFloat(it)
                is IntSize -> NavIntSize.fromComposeIntSize(it)
                is IntOffset -> NavIntOffset.fromComposeIntOffset(it)
                else -> error("Type: ${it::class.simpleName} is not supported.")
            }
        }
    ) as NavFiniteAnimationSpec<T>
}

inline fun <reified T> navTween(
    durationMillis: Int = AnimationConstants.DefaultDurationMillis,
    delayMillis: Int = 0,
    easing: NavEasing = NavEasing.FastOutSlowIn
): NavFiniteAnimationSpec<T> {
    return NavTweenSpec(
        durationMillis = durationMillis,
        delay = delayMillis,
        easing = easing
    )
}

fun <T> navSnap(delayMillis: Int = 0) = NavSnapSpec<T>(delayMillis)

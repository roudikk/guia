package com.roudikk.navigator.animation

import android.os.Parcelable
import androidx.compose.animation.core.*
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import kotlinx.parcelize.Parcelize

/**
 * Mapped specs from Compose to savable navigation specs.
 *
 * Currently the supported animation specs are: tween, spring and snap.
 *
 * [TweenSpec] is converted to [NavigationTweenSpec]
 * [SpringSpec] is converted to [NavigationSpringSpec]
 * [SnapSpec] is converted to [NavigationSnapSpec]
 */

sealed interface NavigationFiniteAnimationSpec<T> : Parcelable {

    fun toComposeSpec(): FiniteAnimationSpec<T>
}

@Parcelize
@JvmInline
value class SpecFloat(val value: Float) : Parcelable

@Parcelize
data class NavigationSpringSpec<T : Parcelable>(
    private val dampingRatio: Float = Spring.DampingRatioNoBouncy,
    private val stiffness: Float = Spring.StiffnessMedium,
    private val visibilityThreshold: T? = null
) : NavigationFiniteAnimationSpec<T> {

    @Suppress("UNCHECKED_CAST")
    override fun toComposeSpec(): FiniteAnimationSpec<T> {
        return SpringSpec(
            dampingRatio = dampingRatio,
            stiffness = stiffness,
            visibilityThreshold = when (visibilityThreshold) {
                is SpecFloat -> visibilityThreshold.value
                is NavigationIntOffset -> visibilityThreshold.toComposeIntOffset()
                is NavigationIntSize -> visibilityThreshold.toComposeIntSize()
                else -> null
            }
        ) as FiniteAnimationSpec<T>
    }
}

@Parcelize
data class NavigationTweenSpec<T>(
    val durationMillis: Int,
    val delay: Int,
    val easing: NavigationEasing
) : NavigationFiniteAnimationSpec<T> {

    override fun toComposeSpec(): FiniteAnimationSpec<T> {
        return TweenSpec(
            durationMillis = durationMillis,
            delay = delay,
            easing = easing.toComposeEasing()
        )
    }
}

@Parcelize
data class NavigationSnapSpec<T>(
    val delayMillis: Int
) : NavigationFiniteAnimationSpec<T> {

    override fun toComposeSpec(): FiniteAnimationSpec<T> {
        return SnapSpec(delayMillis)
    }
}

@Parcelize
enum class NavigationEasing : Parcelable {
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
inline fun <reified T> navigationSpring(
    dampingRatio: Float = Spring.DampingRatioNoBouncy,
    stiffness: Float = Spring.StiffnessMedium,
    visibilityThreshold: T? = null
): NavigationFiniteAnimationSpec<T> {
    return NavigationSpringSpec(
        dampingRatio = dampingRatio,
        stiffness = stiffness,
        visibilityThreshold = visibilityThreshold?.let {
            when (it) {
                is Float -> SpecFloat(it)
                is IntSize -> NavigationIntSize.fromComposeIntSize(it)
                is IntOffset -> NavigationIntOffset.fromComposeIntOffset(it)
                else -> error("Type: ${it::class.simpleName} is not supported.")
            }
        }
    ) as NavigationFiniteAnimationSpec<T>
}

inline fun <reified T> navigationTween(
    durationMillis: Int = AnimationConstants.DefaultDurationMillis,
    delayMillis: Int = 0,
    easing: NavigationEasing = NavigationEasing.FastOutSlowIn
): NavigationFiniteAnimationSpec<T> {
    return NavigationTweenSpec(
        durationMillis = durationMillis,
        delay = delayMillis,
        easing = easing
    )
}

fun <T> navigationSnap(delayMillis: Int = 0) = NavigationSnapSpec<T>(delayMillis)

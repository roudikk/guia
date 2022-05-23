@file:Suppress("unused")

package com.roudikk.navigator.animation

import android.os.Parcelable
import androidx.compose.animation.*
import com.roudikk.navigator.core.NavigationNode
import kotlinx.parcelize.Parcelize

/**
 * One to one match of [ExitTransition] but [Parcelable] so it can be saved and restored.
 *
 * This is used to save the transition state used when navigation between [NavigationNode]s.
 */
@Parcelize
data class NavExitTransition internal constructor(
    internal val data: NavTransitionData
) : Parcelable {

    operator fun plus(enter: NavExitTransition): NavExitTransition {
        return NavExitTransition(
            NavTransitionData(
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
                            NavIntSize.fromComposeIntSize(it)
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
                            NavIntSize.fromComposeIntSize(it)
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
        val None: NavExitTransition = NavExitTransition(NavTransitionData())
    }
}

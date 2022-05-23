package com.roudikk.navigator.animation

import android.os.Parcelable
import androidx.compose.animation.*
import com.roudikk.navigator.core.NavigationNode
import kotlinx.parcelize.Parcelize

/**
 * One to one match of [EnterTransition] but [Parcelable] so it can be saved and restored.
 *
 * This is used to save the transition state used when navigation between [NavigationNode]s.
 */
@Parcelize
data class NavEnterTransition internal constructor(
    internal val data: NavTransitionData
) : Parcelable {

    operator fun plus(enter: NavEnterTransition): NavEnterTransition {
        return NavEnterTransition(
            NavTransitionData(
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
                            NavIntSize.fromComposeIntSize(it)
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
        val None: NavEnterTransition = NavEnterTransition(NavTransitionData())
    }
}

@file:OptIn(ExperimentalAnimationApi::class)

package com.roudikk.navigator

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.snap
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.expandIn
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.animation.shrinkOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideIn
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOut
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import com.google.common.truth.Truth.assertThat
import com.roudikk.navigator.animation.NavAlignment
import com.roudikk.navigator.animation.NavEasing
import com.roudikk.navigator.animation.NavIntOffset
import com.roudikk.navigator.animation.navSnap
import com.roudikk.navigator.animation.navSpring
import com.roudikk.navigator.animation.navTween
import com.roudikk.navigator.animation.transitions.navExpandHorizontally
import com.roudikk.navigator.animation.transitions.navExpandIn
import com.roudikk.navigator.animation.transitions.navExpandVertically
import com.roudikk.navigator.animation.transitions.navFadeIn
import com.roudikk.navigator.animation.transitions.navFadeOut
import com.roudikk.navigator.animation.transitions.navScaleIn
import com.roudikk.navigator.animation.transitions.navScaleOut
import com.roudikk.navigator.animation.transitions.navShrinkHorizontally
import com.roudikk.navigator.animation.transitions.navShrinkOut
import com.roudikk.navigator.animation.transitions.navShrinkVertically
import com.roudikk.navigator.animation.transitions.navSlideIn
import com.roudikk.navigator.animation.transitions.navSlideInHorizontally
import com.roudikk.navigator.animation.transitions.navSlideInVertically
import com.roudikk.navigator.animation.transitions.navSlideOut
import com.roudikk.navigator.animation.transitions.navSlideOutHorizontally
import com.roudikk.navigator.animation.transitions.navSlideOutVertically
import org.junit.Test
import org.junit.jupiter.api.assertThrows
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.isAccessible

class NavigationTransitionsTest {

    private val exitDataField = ExitTransition::class.memberProperties
        .first { it.name == "data" }.apply { isAccessible = true }

    private val enterDataField = EnterTransition::class.memberProperties
        .first { it.name == "data" }.apply { isAccessible = true }

    @Test
    fun `Navigation Tween Spec maps to Compose Tween Spec`() {

        NavEasing.values().forEach { easing ->
            val durationMillis = (0..300).random()
            val delayMillis = (0..300).random()

            val navTween = navTween<Any>(
                durationMillis = durationMillis,
                delayMillis = delayMillis,
                easing = easing
            ).toComposeSpec()

            val composeTween = tween<Any>(
                durationMillis = durationMillis,
                delayMillis = delayMillis,
                easing = easing.toComposeEasing()
            )

            assertThat(navTween).isEqualTo(composeTween)
        }
    }

    @Test
    fun `Navigation Spring Spec maps to Compose Spring Spec`() {
        // Test Float, IntSize and IntOffset
        listOf(
            5f,
            IntSize(100, 100),
            IntOffset(5, 10)
        ).forEach {
            val dampingRatio = Spring.DampingRatioLowBouncy
            val stiffness = Spring.StiffnessVeryLow

            val navSpringSpec = navSpring(
                dampingRatio = dampingRatio,
                stiffness = stiffness,
                visibilityThreshold = it
            ).toComposeSpec()

            val composeSpringSpec = spring(
                dampingRatio = dampingRatio,
                stiffness = stiffness,
                visibilityThreshold = it
            )

            assertThat(navSpringSpec).isEqualTo(composeSpringSpec)
        }

        // Assert that other types should throw an exception
        assertThrows<IllegalStateException> { navSpring(visibilityThreshold = 1) }
        assertThrows<IllegalStateException> { navSpring(visibilityThreshold = "Test") }
        assertThrows<IllegalStateException> { navSpring(visibilityThreshold = 1.0) }
    }

    @Test
    fun `Navigation Snap Spec maps to Compose Snap Spec`() {

        val navSnapSpec = navSnap<Any>(delayMillis = 100)
            .toComposeSpec()
        val composeSpec = snap<Any>(delayMillis = 100)

        assertThat(navSnapSpec).isEqualTo(composeSpec)
    }

    @Test
    fun `Navigation Fade In maps to Compose Fade In`() {

        val navTransition = navFadeIn(initialAlpha = 0.5f)
            .toComposeEnterTransition()

        val composeTransition = fadeIn(initialAlpha = 0.5f)

        assertThat(navTransition).isEqualTo(composeTransition)
    }

    @Test
    fun `Navigation Fade Out maps to Compose Fade Out`() {

        val navTransition = navFadeOut(targetAlpha = 0.5f)
            .toComposeExitTransition()

        val composeTransition = fadeOut(targetAlpha = 0.5f)

        assertThat(navTransition).isEqualTo(composeTransition)
    }

    @Test
    fun `Navigation Slide In maps to Compose Slide In`() {

        val navTransition = navSlideIn { NavIntOffset(it.width, it.height) }
            .toComposeEnterTransition()

        val composeTransition = slideIn(initialOffset = { fullSize: IntSize ->
            IntOffset(fullSize.width, fullSize.height)
        })

        assertThat(enterDataField.get(composeTransition).toString())
            .isEqualTo(enterDataField.get(navTransition).toString())
    }

    @Test
    fun `Navigation Slide Out maps to Compose Slide Out`() {

        val navTransition = navSlideOut { NavIntOffset(it.width, it.height) }
            .toComposeExitTransition()

        val composeTransition = slideOut {
            IntOffset(it.width, it.height)
        }

        assertThat(exitDataField.get(composeTransition).toString())
            .isEqualTo(exitDataField.get(navTransition).toString())
    }

    @Test
    fun `Navigation Slide In Vertically maps to Compose Slide In Vertically`() {

        val navTransition = navSlideInVertically {
            it
        }.toComposeEnterTransition()

        val composeTransition = slideInVertically {
            it
        }

        assertThat(enterDataField.get(composeTransition).toString())
            .isEqualTo(enterDataField.get(navTransition).toString())
    }

    @Test
    fun `Navigation Slide Out Vertically maps to Compose Slide Out Vertically`() {

        val navTransition = navSlideOutVertically {
            it
        }.toComposeExitTransition()

        val composeTransition = slideOutVertically {
            it
        }

        assertThat(exitDataField.get(composeTransition).toString())
            .isEqualTo(exitDataField.get(navTransition).toString())
    }

    @Test
    fun `Navigation Slide In Horizontally maps to Compose Slide In Horizontally`() {

        val navTransition = navSlideInHorizontally {
            it
        }.toComposeEnterTransition()

        val composeTransition = slideInHorizontally {
            it
        }

        assertThat(enterDataField.get(composeTransition).toString())
            .isEqualTo(enterDataField.get(navTransition).toString())
    }

    @Test
    fun `Navigation Slide Out Horizontally maps to Compose Slide Out Horizontally`() {

        val navTransition = navSlideOutHorizontally {
            it
        }.toComposeExitTransition()

        val composeTransition = slideOutHorizontally {
            it
        }

        assertThat(exitDataField.get(composeTransition).toString())
            .isEqualTo(exitDataField.get(navTransition).toString())
    }

    @Test
    fun `Navigation Scale In maps to Compose Scale In`() {

        val navTransition = navScaleIn(
            initialScale = 0.5f,
            transformOrigin = TransformOrigin.Center
        ).toComposeEnterTransition()

        val composeTransition = scaleIn(
            initialScale = 0.5f,
            transformOrigin = TransformOrigin.Center
        )

        assertThat(navTransition).isEqualTo(composeTransition)
    }

    @Test
    fun `Navigation Scale Out maps to Compose Scale Out`() {

        val navTransition = navScaleOut(
            targetScale = 0.5f,
            transformOrigin = TransformOrigin.Center
        ).toComposeExitTransition()

        val composeTransition = scaleOut(
            targetScale = 0.5f,
            transformOrigin = TransformOrigin.Center
        )

        assertThat(navTransition).isEqualTo(composeTransition)
    }

    @Test
    fun `Navigation Expand In maps to Compose Expand In`() {

        val navTransition = navExpandIn {
            it
        }.toComposeEnterTransition()

        val composeTransition = expandIn {
            it
        }

        assertThat(enterDataField.get(composeTransition).toString())
            .isEqualTo(enterDataField.get(navTransition).toString())
    }

    @Test
    fun `Navigation Shrink Out maps to Compose Shrink Out`() {

        val navTransition = navShrinkOut {
            it
        }.toComposeExitTransition()

        val composeTransition = shrinkOut {
            it
        }

        assertThat(exitDataField.get(composeTransition).toString())
            .isEqualTo(exitDataField.get(navTransition).toString())
    }

    @Test
    fun `Navigation Expand Vertically maps to Compose Expand Vertically`() {

        val navTransition = navExpandVertically {
            it
        }.toComposeEnterTransition()

        val composeTransition = expandVertically {
            it
        }

        assertThat(enterDataField.get(composeTransition).toString())
            .isEqualTo(enterDataField.get(navTransition).toString())
    }

    @Test
    fun `Navigation Shrink Vertically maps to Compose Shrink Vertically`() {

        val navTransition = navShrinkVertically {
            it
        }.toComposeExitTransition()

        val composeTransition = shrinkVertically {
            it
        }

        assertThat(exitDataField.get(composeTransition).toString())
            .isEqualTo(exitDataField.get(navTransition).toString())
    }

    @Test
    fun `Navigation Expand Horizontally maps to Compose Expand Horizontally`() {

        val navTransition = navExpandHorizontally {
            it
        }.toComposeEnterTransition()

        val composeTransition = expandHorizontally {
            it
        }

        assertThat(enterDataField.get(composeTransition).toString())
            .isEqualTo(enterDataField.get(navTransition).toString())
    }

    @Test
    fun `Navigation Shrink Horizontally maps to Compose Shrink Horizontally`() {

        val navTransition = navShrinkHorizontally {
            it
        }.toComposeExitTransition()

        val composeTransition = shrinkHorizontally {
            it
        }

        assertThat(exitDataField.get(composeTransition).toString())
            .isEqualTo(exitDataField.get(navTransition).toString())
    }

    @Test
    fun `Combined nav enter transitions maps to combined compose transitions`() {

        val navTransition = (
            navFadeIn() + navScaleIn() +
                navExpandIn(expandFrom = NavAlignment.Center) { it } +
                navSlideIn {
                    NavIntOffset(
                        it.width,
                        it.height
                    )
                }
            )
            .toComposeEnterTransition()

        val composeTransition = fadeIn() + scaleIn() +
            expandIn(expandFrom = Alignment.Center) { it } +
            slideIn { IntOffset(it.width, it.height) }

        assertThat(enterDataField.get(composeTransition).toString())
            .isEqualTo(enterDataField.get(navTransition).toString())
    }

    @Test
    fun `Combined nav exit transitions maps to combined compose transitions`() {

        val navTransition = (
            navFadeOut() + navScaleOut() +
                navShrinkOut(shrinkTowards = NavAlignment.Center) { it } +
                navSlideOut {
                    NavIntOffset(
                        it.width,
                        it.height
                    )
                }
            )
            .toComposeExitTransition()

        val composeTransition = fadeOut() + scaleOut() +
            shrinkOut(shrinkTowards = Alignment.Center) { it } +
            slideOut { IntOffset(it.width, it.height) }

        assertThat(exitDataField.get(composeTransition).toString())
            .isEqualTo(exitDataField.get(navTransition).toString())
    }
}

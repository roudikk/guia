@file:OptIn(ExperimentalAnimationApi::class)

package com.roudikk.compose_navigator

import androidx.compose.animation.*
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.snap
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import com.google.common.truth.Truth.assertThat
import com.roudikk.compose_navigator.animation.*
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

        NavigationEasing.values().forEach { easing ->
            val durationMillis = (0..300).random()
            val delayMillis = (0..300).random()

            val navigationTween = navigationTween<Any>(
                durationMillis = durationMillis,
                delayMillis = delayMillis,
                easing = easing
            ).toComposeSpec()

            val composeTween = tween<Any>(
                durationMillis = durationMillis,
                delayMillis = delayMillis,
                easing = easing.toComposeEasing()
            )

            assertThat(navigationTween).isEqualTo(composeTween)
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

            val navigationSpringSpec = navigationSpring(
                dampingRatio = dampingRatio,
                stiffness = stiffness,
                visibilityThreshold = it
            ).toComposeSpec()

            val composeSpringSpec = spring(
                dampingRatio = dampingRatio,
                stiffness = stiffness,
                visibilityThreshold = it
            )

            assertThat(navigationSpringSpec).isEqualTo(composeSpringSpec)
        }

        // Assert that other types should throw an exception
        assertThrows<IllegalStateException> { navigationSpring(visibilityThreshold = 1) }
        assertThrows<IllegalStateException> { navigationSpring(visibilityThreshold = "Test") }
        assertThrows<IllegalStateException> { navigationSpring(visibilityThreshold = 1.0) }
    }

    @Test
    fun `Navigation Snap Spec maps to Compose Snap Spec`() {

        val navigationSnapSpec = navigationSnap<Any>(delayMillis = 100)
            .toComposeSpec()
        val composeSpec = snap<Any>(delayMillis = 100)

        assertThat(navigationSnapSpec).isEqualTo(composeSpec)
    }

    @Test
    fun `Navigation Fade In maps to Compose Fade In`() {

        val navigationTransition = navigationFadeIn(initialAlpha = 0.5f)
            .toComposeEnterTransition()

        val composeTransition = fadeIn(initialAlpha = 0.5f)

        assertThat(navigationTransition).isEqualTo(composeTransition)
    }

    @Test
    fun `Navigation Fade Out maps to Compose Fade Out`() {

        val navigationTransition = navigationFadeOut(targetAlpha = 0.5f)
            .toComposeExitTransition()

        val composeTransition = fadeOut(targetAlpha = 0.5f)

        assertThat(navigationTransition).isEqualTo(composeTransition)
    }

    @Test
    fun `Navigation Slide In maps to Compose Slide In`() {

        val navigationTransition = navigationSlideIn {
            NavigationIntOffset(it.width, it.height)
        }.toComposeEnterTransition()

        val composeTransition = slideIn(initialOffset = { fullSize: IntSize ->
            IntOffset(fullSize.width, fullSize.height)
        })

        assertThat(enterDataField.get(composeTransition).toString())
            .isEqualTo(enterDataField.get(navigationTransition).toString())
    }

    @Test
    fun `Navigation Slide Out maps to Compose Slide Out`() {

        val navigationTransition = navigationSlideOut {
            NavigationIntOffset(it.width, it.height)
        }.toComposeExitTransition()

        val composeTransition = slideOut {
            IntOffset(it.width, it.height)
        }

        assertThat(exitDataField.get(composeTransition).toString())
            .isEqualTo(exitDataField.get(navigationTransition).toString())
    }

    @Test
    fun `Navigation Slide In Vertically maps to Compose Slide In Vertically`() {

        val navigationTransition = navigationSlideInVertically {
            it
        }.toComposeEnterTransition()

        val composeTransition = slideInVertically {
            it
        }

        assertThat(enterDataField.get(composeTransition).toString())
            .isEqualTo(enterDataField.get(navigationTransition).toString())
    }

    @Test
    fun `Navigation Slide Out Vertically maps to Compose Slide Out Vertically`() {

        val navigationTransition = navigationSlideOutVertically() {
            it
        }.toComposeExitTransition()

        val composeTransition = slideOutVertically {
            it
        }

        assertThat(exitDataField.get(composeTransition).toString())
            .isEqualTo(exitDataField.get(navigationTransition).toString())
    }

    @Test
    fun `Navigation Slide In Horizontally maps to Compose Slide In Horizontally`() {

        val navigationTransition = navigationSlideInHorizontally {
            it
        }.toComposeEnterTransition()

        val composeTransition = slideInHorizontally {
            it
        }

        assertThat(enterDataField.get(composeTransition).toString())
            .isEqualTo(enterDataField.get(navigationTransition).toString())
    }

    @Test
    fun `Navigation Slide Out Horizontally maps to Compose Slide Out Horizontally`() {

        val navigationTransition = navigationSlideOutHorizontally {
            it
        }.toComposeExitTransition()

        val composeTransition = slideOutHorizontally {
            it
        }

        assertThat(exitDataField.get(composeTransition).toString())
            .isEqualTo(exitDataField.get(navigationTransition).toString())
    }

    @Test
    fun `Navigation Scale In maps to Compose Scale In`() {

        val navigationTransition = navigationScaleIn(
            initialScale = 0.5f,
            transformOrigin = TransformOrigin.Center
        ).toComposeEnterTransition()

        val composeTransition = scaleIn(
            initialScale = 0.5f,
            transformOrigin = TransformOrigin.Center
        )

        assertThat(navigationTransition).isEqualTo(composeTransition)
    }

    @Test
    fun `Navigation Scale Out maps to Compose Scale Out`() {

        val navigationTransition = navigationScaleOut(
            targetScale = 0.5f,
            transformOrigin = TransformOrigin.Center
        ).toComposeExitTransition()

        val composeTransition = scaleOut(
            targetScale = 0.5f,
            transformOrigin = TransformOrigin.Center
        )

        assertThat(navigationTransition).isEqualTo(composeTransition)
    }

    @Test
    fun `Navigation Expand In maps to Compose Expand In`() {

        val navigationTransition = navigationExpandIn {
            it
        }.toComposeEnterTransition()

        val composeTransition = expandIn {
            it
        }

        assertThat(enterDataField.get(composeTransition).toString())
            .isEqualTo(enterDataField.get(navigationTransition).toString())
    }

    @Test
    fun `Navigation Shrink Out maps to Compose Shrink Out`() {

        val navigationTransition = navigationShrinkOut {
            it
        }.toComposeExitTransition()

        val composeTransition = shrinkOut {
            it
        }

        assertThat(exitDataField.get(composeTransition).toString())
            .isEqualTo(exitDataField.get(navigationTransition).toString())
    }

    @Test
    fun `Navigation Expand Vertically maps to Compose Expand Vertically`() {

        val navigationTransition = navigationExpandVertically {
            it
        }.toComposeEnterTransition()

        val composeTransition = expandVertically {
            it
        }

        assertThat(enterDataField.get(composeTransition).toString())
            .isEqualTo(enterDataField.get(navigationTransition).toString())
    }

    @Test
    fun `Navigation Shrink Vertically maps to Compose Shrink Vertically`() {

        val navigationTransition = navigationShrinkVertically {
            it
        }.toComposeExitTransition()

        val composeTransition = shrinkVertically {
            it
        }

        assertThat(exitDataField.get(composeTransition).toString())
            .isEqualTo(exitDataField.get(navigationTransition).toString())
    }

    @Test
    fun `Navigation Expand Horizontally maps to Compose Expand Horizontally`() {

        val navigationTransition = navigationExpandHorizontally {
            it
        }.toComposeEnterTransition()

        val composeTransition = expandHorizontally {
            it
        }

        assertThat(enterDataField.get(composeTransition).toString())
            .isEqualTo(enterDataField.get(navigationTransition).toString())
    }

    @Test
    fun `Navigation Shrink Horizontally maps to Compose Shrink Horizontally`() {

        val navigationTransition = navigationShrinkHorizontally {
            it
        }.toComposeExitTransition()

        val composeTransition = shrinkHorizontally {
            it
        }

        assertThat(exitDataField.get(composeTransition).toString())
            .isEqualTo(exitDataField.get(navigationTransition).toString())
    }

    @Test
    fun `Combined navigation enter transitions maps to combined compose transitions`() {

        val navigationTransition = (navigationFadeIn() + navigationScaleIn() +
                navigationExpandIn(expandFrom = NavigationAlignment.Center) { it } +
                navigationSlideIn { NavigationIntOffset(it.width, it.height) })
            .toComposeEnterTransition()

        val composeTransition = fadeIn() + scaleIn() +
                expandIn(expandFrom = Alignment.Center) { it } +
                slideIn { IntOffset(it.width, it.height) }

        assertThat(enterDataField.get(composeTransition).toString())
            .isEqualTo(enterDataField.get(navigationTransition).toString())
    }

    @Test
    fun `Combined navigation exit transitions maps to combined compose transitions`() {

        val navigationTransition = (navigationFadeOut() + navigationScaleOut() +
                navigationShrinkOut(shrinkTowards = NavigationAlignment.Center) { it } +
                navigationSlideOut { NavigationIntOffset(it.width, it.height) })
            .toComposeExitTransition()

        val composeTransition = fadeOut() + scaleOut() +
                shrinkOut(shrinkTowards = Alignment.Center) { it } +
                slideOut { IntOffset(it.width, it.height) }

        assertThat(exitDataField.get(composeTransition).toString())
            .isEqualTo(exitDataField.get(navigationTransition).toString())
    }
}
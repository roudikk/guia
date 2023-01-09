package com.roudikk.navigator.containers

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.snap
import androidx.compose.animation.fadeOut
import androidx.compose.animation.with
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.roudikk.navigator.animation.EnterExitTransition
import com.roudikk.navigator.animation.ProvideNavigationVisibilityScope
import com.roudikk.navigator.backstack.LifeCycleEntry
import com.roudikk.navigator.containers.BottomSheetValue.Expanded
import com.roudikk.navigator.containers.BottomSheetValue.Hidden
import com.roudikk.navigator.core.BottomSheet
import com.roudikk.navigator.core.Navigator
import com.roudikk.navigator.core.navigationNode
import com.roudikk.navigator.extensions.popBackstack

private fun Navigator.currentBottomSheet(): BottomSheet? {
    return backStack.last().let(::navigationNode) as? BottomSheet
}

/**
 * Renders a Compose BottomSheet if a [Navigator]'s current entry is a [BottomSheet].
 */
@Composable
internal fun Navigator.BottomSheetContainer(
    content: @Composable (LifeCycleEntry) -> Unit,
    bottomSheetEntry: LifeCycleEntry?
) {
    val bottomSheet = currentBottomSheet()
    val confirmStateChange = remember(bottomSheet) {
        { sheetValue: BottomSheetValue ->
            bottomSheet?.let {
                it.bottomSheetOptions.confirmStateChange(sheetValue)
            } ?: true
        }
    }

    val bottomSheetState = rememberBottomSheetState(
        initialValue = bottomSheetEntry?.let { Expanded } ?: Hidden,
        confirmStateChange = confirmStateChange
    )

    BottomSheetLayout(
        modifier = Modifier.fillMaxSize(),
        sheetState = bottomSheetState,
        scrimColor = bottomSheet?.bottomSheetOptions?.scrimColor
            ?: MaterialTheme.colors.onSurface.copy(alpha = 0.12F)
    ) {
        BottomSheetContent(
            sheetState = bottomSheetState,
            bottomSheetEntry = bottomSheetEntry,
            currentTransition = currentTransition,
            content = content
        )
    }

    // Make sure the bottom sheet is shown when the bottom sheet entry is available.
    LaunchedEffect(bottomSheetEntry) {
        if (bottomSheetEntry != null) {
            bottomSheetState.show()
        } else {
            bottomSheetState.hide()
        }
    }

    // If the user swipes the bottom sheet down, the state would be updated to 'Hidden'
    // So we make sure to pop the back stack so the state of this container updates and the sheet is
    // hidden.
    LaunchedEffect(bottomSheetState.currentValue) {
        if (bottomSheetEntry != null && bottomSheetState.currentValue == Hidden) {
            popBackstack()
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun BottomSheetContent(
    sheetState: BottomSheetState,
    bottomSheetEntry: LifeCycleEntry?,
    currentTransition: EnterExitTransition,
    content: @Composable (LifeCycleEntry) -> Unit
) {
    Surface(
        shape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)
    ) {
        val density = LocalDensity.current

        AnimatedContent(
            targetState = bottomSheetEntry,
            transitionSpec = {
                val enterTransition = when {
                    initialState == null -> EnterTransition.None
                    sheetState.currentValue == Hidden -> EnterTransition.None
                    else -> currentTransition.enter
                }

                val exitTransition = when {
                    initialState == null && targetState != null -> ExitTransition.None
                    targetState == null -> fadeOut(animationSpec = snap(delayMillis = 300))
                    else -> currentTransition.exit
                }

                enterTransition with exitTransition
            }
        ) { bottomSheetEntry ->
            if (bottomSheetEntry != null) {
                Box(
                    modifier = Modifier.onGloballyPositioned {
                        sheetState.sheetHeight = it.size.height.toFloat()
                    }
                ) {
                    ProvideNavigationVisibilityScope {
                        content(bottomSheetEntry)
                    }
                }
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(with(density) { sheetState.sheetHeight?.toDp() ?: 1.dp })
                )
            }
        }
    }
}

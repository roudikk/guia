package com.roudikk.guia.containers

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.snap
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.roudikk.guia.animation.EnterExitTransition
import com.roudikk.guia.animation.ProvideNavVisibilityScope
import com.roudikk.guia.containers.BottomSheetValue.Expanded
import com.roudikk.guia.containers.BottomSheetValue.Hidden
import com.roudikk.guia.core.BottomSheet
import com.roudikk.guia.core.Navigator
import com.roudikk.guia.core.navigationNode
import com.roudikk.guia.core.nodeTransition
import com.roudikk.guia.extensions.pop
import com.roudikk.guia.lifecycle.LifecycleEntry
import kotlinx.coroutines.launch

private fun Navigator.currentBottomSheet(): BottomSheet? {
    return backstack.lastOrNull()?.let(::navigationNode) as? BottomSheet
}

/**
 * Renders a Compose BottomSheet if a [Navigator]'s current entry is a [BottomSheet].
 */
@Composable
fun Navigator.BottomSheetContainer(
    container: Container,
    bottomSheetEntry: LifecycleEntry?,
    bottomSheetScrimColor: Color,
    content: @Composable (LifecycleEntry) -> Unit
) {
    val scope = rememberCoroutineScope()
    val bottomSheet = currentBottomSheet()
    val confirmStateChange = { sheetValue: BottomSheetValue ->
        currentBottomSheet()?.let {
            it.bottomSheetOptions.confirmStateChange(sheetValue)
        } ?: true
    }

    val bottomSheetState = rememberBottomSheetState(
        initialValue = bottomSheetEntry?.let { Expanded } ?: Hidden,
        confirmStateChange = confirmStateChange
    )

    BottomSheetLayout(
        modifier = Modifier.fillMaxSize(),
        sheetState = bottomSheetState,
        onClickOutside = {
            currentBottomSheet()?.let {
                it.bottomSheetOptions.onOutsideClick()
                if (it.bottomSheetOptions.dismissOnClickOutside) {
                    if (bottomSheetState.confirmStateChange(Hidden)) {
                        scope.launch { bottomSheetState.hide() }
                    }
                }
            }
        },
        scrimColor = bottomSheet?.bottomSheetOptions?.scrimColor ?: bottomSheetScrimColor
    ) {
        Box(
            modifier = Modifier.onGloballyPositioned {
                if (bottomSheetEntry != null) {
                    bottomSheetState.sheetHeight = it.size.height.toFloat()
                }
            }
        ) {
            container {
                BottomSheetContent(
                    sheetState = bottomSheetState,
                    bottomSheetEntry = bottomSheetEntry,
                    currentTransition = nodeTransition<BottomSheet>(),
                    content = content
                )
            }
        }
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
            pop()
        }
    }

    // Reset the sheet height back to null when the sheet is dismissed.
    LaunchedEffect(bottomSheetState.currentValue) {
        if (bottomSheetState.currentValue == Hidden) {
            bottomSheetState.sheetHeight = null
        }
    }
}

@Composable
private fun BottomSheetContent(
    sheetState: BottomSheetState,
    bottomSheetEntry: LifecycleEntry?,
    currentTransition: EnterExitTransition,
    content: @Composable (LifecycleEntry) -> Unit
) {
    val density = LocalDensity.current

    AnimatedContent(
        label = "BottomSheetContent_entry",
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

            enterTransition togetherWith exitTransition
        }
    ) { targetEntry ->
        if (targetEntry != null) {
            Box(modifier = Modifier.testTag("bottom_sheet_container")) {
                ProvideNavVisibilityScope {
                    content(targetEntry)
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

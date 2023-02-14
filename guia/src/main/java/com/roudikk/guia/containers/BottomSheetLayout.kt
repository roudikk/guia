package com.roudikk.guia.containers

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.SwipeableDefaults
import androidx.compose.material.SwipeableState
import androidx.compose.material.swipeable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.TopCenter
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.dismiss
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.Velocity
import com.roudikk.guia.containers.BottomSheetValue.Expanded
import com.roudikk.guia.containers.BottomSheetValue.Hidden
import kotlinx.coroutines.launch
import kotlin.math.max
import kotlin.math.roundToInt

/**
 * A copy of [ModalBottomSheetLayout] that works better with the navigation system.
 *
 * - No Surface is used around the content, the content can have any Surface behind it, including
 * Material 2 or Material 3 surfaces.
 * - The content is aligned automatically in the center.
 * - The sheet container does not take full width, this means the left/right side of a bottom sheet
 * that doesn't take up the full width is no longer swipeable in those empty spaces, instead clicking
 * those spaces will now properly dismiss the sheet.
 * - The sheet state is not saveable, this is because the initial state is decided by the navigation's
 * bottom sheet entry. This has better result when navigating back to a container that has a BottomSheet
 * as its last entry, instead of it sliding up every time, it would already be in the expanded state.
 * - For some reason using [ModalBottomSheetLayout] the content would not be interactable for a split
 * second after it changes, clicking on a button in the new content would simply not trigger.
 * - the sheet height is now calculated in [BottomSheetContainer], which means the consumer would
 * need to report back the height to this layout to function properly. The reason behind this is
 * the content is wrapped in an [AnimatedContent] and we need to use the new sheet's height as the current
 * height even when animating, this results in a much smoother animation between sheets that have
 * different heights.
 * - Allows the ability to override outside click behaviour.
 */
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun BottomSheetLayout(
    modifier: Modifier = Modifier,
    sheetState: BottomSheetState = rememberBottomSheetState(Hidden),
    onClickOutside: () -> Unit,
    scrimColor: Color,
    sheetContent: @Composable () -> Unit
) {
    val scope = rememberCoroutineScope()

    BoxWithConstraints(modifier) {
        val fullHeight = constraints.maxHeight.toFloat()
        val sheetHeight = sheetState.sheetHeight
        val anchors = remember(sheetHeight) {
            sheetHeight?.let {
                if (sheetHeight < fullHeight / 2) {
                    mapOf(
                        fullHeight to Hidden,
                        fullHeight - sheetHeight to Expanded
                    )
                } else {
                    mapOf(
                        fullHeight to Hidden,
                        max(0f, fullHeight - sheetHeight) to Expanded
                    )
                }
            } ?: emptyMap()
        }

        Box(Modifier.fillMaxSize()) {
            Scrim(
                color = scrimColor,
                onDismiss = onClickOutside,
                visible = sheetState.targetValue != Hidden
            )
        }

        Box(
            Modifier
                .align(TopCenter)
                .nestedScroll(sheetState.nestedScrollConnection)
                .offset {
                    val y = if (anchors.isEmpty()) {
                        // if we don't know our anchors yet, render the sheet as hidden.
                        fullHeight.roundToInt()
                    } else {
                        // if we do know our anchors, respect them.
                        sheetState.offset.value.roundToInt()
                    }

                    IntOffset(0, y)
                }
                .bottomSheetSwipeable(sheetState, anchors)
                .semantics {
                    if (sheetState.isVisible) {
                        dismiss {
                            if (sheetState.confirmStateChange(Hidden)) {
                                scope.launch { sheetState.hide() }
                            }
                            true
                        }
                    }
                },
            contentAlignment = Alignment.BottomCenter
        ) {
            sheetContent()
        }
    }
}

@Suppress("ModifierInspectorInfo")
@OptIn(ExperimentalMaterialApi::class)
private fun Modifier.bottomSheetSwipeable(
    sheetState: BottomSheetState,
    anchors: Map<Float, BottomSheetValue>
): Modifier {
    val modifier = if (anchors.isNotEmpty()) {
        Modifier.swipeable(
            state = sheetState,
            anchors = anchors,
            orientation = Orientation.Vertical,
            enabled = sheetState.currentValue != Hidden,
            resistance = null
        )
    } else {
        Modifier
    }

    return this.then(modifier)
}

@Composable
private fun Scrim(
    color: Color,
    onDismiss: () -> Unit,
    visible: Boolean
) {
    val animatedColor by animateColorAsState(color)
    val resources = LocalContext.current.resources
    val alpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween()
    )

    // We want to use Composes "Close sheet" announcement for TalkBack.
    @SuppressLint("PrivateResource")
    val closeSheet = resources.getString(androidx.compose.ui.R.string.close_sheet)

    val dismissModifier = if (visible) {
        Modifier
            .pointerInput(onDismiss) { detectTapGestures { onDismiss() } }
            .semantics(mergeDescendants = true) {
                contentDescription = closeSheet
                onClick { onDismiss(); true }
            }
    } else {
        Modifier
    }

    Canvas(
        Modifier
            .fillMaxSize()
            .then(dismissModifier)
    ) {
        drawRect(color = animatedColor, alpha = alpha)
    }
}

@ExperimentalMaterialApi
internal val <T> SwipeableState<T>.PreUpPostDownNestedScrollConnection: NestedScrollConnection
    get() = object : NestedScrollConnection {
        override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
            val delta = available.toFloat()
            return if (delta < 0 && source == NestedScrollSource.Drag) {
                performDrag(delta).toOffset()
            } else {
                Offset.Zero
            }
        }

        override fun onPostScroll(
            consumed: Offset,
            available: Offset,
            source: NestedScrollSource
        ): Offset {
            return if (source == NestedScrollSource.Drag) {
                performDrag(available.toFloat()).toOffset()
            } else {
                Offset.Zero
            }
        }

        override suspend fun onPreFling(available: Velocity): Velocity {
            return Velocity.Zero
        }

        override suspend fun onPostFling(consumed: Velocity, available: Velocity): Velocity {
            performFling(velocity = Offset(available.x, available.y).toFloat())
            return available
        }

        private fun Float.toOffset(): Offset = Offset(0f, this)

        private fun Offset.toFloat(): Float = this.y
    }

enum class BottomSheetValue {
    Hidden,
    Expanded,
}

@OptIn(ExperimentalMaterialApi::class)
class BottomSheetState(
    initialValue: BottomSheetValue,
    animationSpec: AnimationSpec<Float> = SwipeableDefaults.AnimationSpec,
    internal var confirmStateChange: (BottomSheetValue) -> Boolean = { true }
) : SwipeableState<BottomSheetValue>(
    initialValue = initialValue,
    animationSpec = animationSpec,
    confirmStateChange = confirmStateChange
) {
    val isVisible: Boolean
        get() = currentValue != Hidden

    var sheetHeight by mutableStateOf<Float?>(null)

    suspend fun show() = animateTo(Expanded)
    suspend fun hide() = animateTo(Hidden)

    internal val nestedScrollConnection = this.PreUpPostDownNestedScrollConnection
}

@Composable
fun rememberBottomSheetState(
    initialValue: BottomSheetValue,
    animationSpec: AnimationSpec<Float> = SwipeableDefaults.AnimationSpec,
    confirmStateChange: (BottomSheetValue) -> Boolean = { true }
): BottomSheetState {
    return remember {
        BottomSheetState(
            initialValue = initialValue,
            animationSpec = animationSpec,
            confirmStateChange = confirmStateChange
        )
    }.apply {
        this.confirmStateChange = confirmStateChange
    }
}

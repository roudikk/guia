package com.roudikk.navigator.containers

import android.util.Log
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.SwipeableDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.roudikk.navigator.backstack.LifeCycleEntry
import com.roudikk.navigator.core.BottomSheet
import com.roudikk.navigator.core.NavigationNode
import com.roudikk.navigator.core.Navigator
import com.roudikk.navigator.core.navigationNode
import com.roudikk.navigator.extensions.popBackstack

/**
 * Creates a [ModalBottomSheetState], however this one is not saveable,
 * since the initial state of the bottom sheet is controlled by the current
 * state of the navigator.
 */
@Composable
@ExperimentalMaterialApi
private fun rememberBottomSheetState(
    initialValue: ModalBottomSheetValue,
    animationSpec: AnimationSpec<Float> = SwipeableDefaults.AnimationSpec,
    confirmStateChange: (ModalBottomSheetValue) -> Boolean = { true }
): ModalBottomSheetState {
    return remember {
        ModalBottomSheetState(
            initialValue = initialValue,
            animationSpec = animationSpec,
            isSkipHalfExpanded = true,
            confirmStateChange = confirmStateChange
        )
    }
}

private fun Navigator.currentBottomSheet(): BottomSheet? {
    return backStack.last().let(::navigationNode) as? BottomSheet
}

/**
 * Renders a Compose BottomSheet if a [Navigator]'s current entry is a [BottomSheet].
 */
@OptIn(ExperimentalMaterialApi::class)
@Composable
internal fun Navigator.BottomSheetContainer(
    content: @Composable (LifeCycleEntry) -> Unit,
    bottomSheetEntry: LifeCycleEntry?,
    bottomSheetSetup: BottomSheetSetup,
    container: @Composable () -> Unit
) {
    val currentBottomSheet = currentBottomSheet()
    val confirmStateChange = { sheetValue: ModalBottomSheetValue ->
        currentBottomSheet()
            .also { Log.d("TEST1", "Sheet: $it") }
            ?.let {
                it.bottomSheetOptions.confirmStateChange(sheetValue)
                    .also { Log.d("TEST1", "Sheet: $it") }
            } ?: true
    }

    val bottomSheetState = rememberBottomSheetState(
        initialValue = if (bottomSheetEntry == null) {
            ModalBottomSheetValue.Hidden
        } else {
            ModalBottomSheetValue.Expanded
        },
        animationSpec = bottomSheetSetup.animationSpec,
        confirmStateChange = {
            confirmStateChange(it)
        }
    )

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
        if (bottomSheetEntry != null && bottomSheetState.currentValue == ModalBottomSheetValue.Hidden) {
            popBackstack()
        }
    }

    ModalBottomSheetLayout(
        content = container,
        sheetState = bottomSheetState,
        modifier = Modifier.fillMaxSize(),
        sheetBackgroundColor = Color.Transparent,
        sheetContentColor = Color.Transparent,
        sheetElevation = 0.dp,
        scrimColor = bottomSheetSetup.scrimColor,
        sheetShape = RoundedCornerShape(0.dp),
        sheetContent = {
            BottomSheetContent(
                bottomSheetSetup = bottomSheetSetup,
                bottomSheetEntry = bottomSheetEntry,
                bottomSheet = currentBottomSheet,
                content = content,
            )
        },
    )
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun ColumnScope.BottomSheetContent(
    bottomSheetSetup: BottomSheetSetup,
    bottomSheetEntry: LifeCycleEntry?,
    bottomSheet: NavigationNode?,
    content: @Composable (LifeCycleEntry) -> Unit
) = Box(
    modifier = Modifier
        .align(Alignment.CenterHorizontally)
        .fillMaxWidth(),
    contentAlignment = Alignment.BottomCenter
) {
    val localDensity = LocalDensity.current

    // The content animation and dismissing of bottom sheet looks weird when the content is suddenly
    // removed, this make sure the content's height is preserved even when its removed so the
    // animations and the dismissal the bottom sheet are smoother/
    var contentHeightDp by remember {
        mutableStateOf(with(localDensity) { 1.toDp() })
    }

    Log.d("TEST", "height: $contentHeightDp")

    bottomSheetSetup.bottomSheetContainer(
        modifier = Modifier
            .then(
                bottomSheetEntry?.backStackEntry
                    ?.let {
                        (bottomSheet as BottomSheet).bottomSheetOptions.modifier
                    } ?: Modifier
            )
    ) {
        if (bottomSheetEntry != null) {
            Box(
                modifier = Modifier
                    .testTag("BottomSheetContainer")
                    .onGloballyPositioned {
                        contentHeightDp = with(localDensity) {
                            it.size.height
                                .toFloat()
                                .toDp()
                        }.also {
                            Log.d("TEST", "$contentHeightDp")
                        }
                    },
                contentAlignment = Alignment.BottomCenter
            ) {
                content(bottomSheetEntry)
            }
        } else {
            Box(
                modifier = Modifier
                    .height(contentHeightDp)
            )
        }
    }
}

/**
 * Provide extra bottom sheet options.
 *
 * @property scrimColor the scrim color behind the bottom sheet and on top of the content behind it.
 * @property bottomSheetContainer use this when you have a single bottom sheet design for all your
 * bottom sheets.
 */
data class BottomSheetSetup(
    val scrimColor: Color = Color.Black.copy(alpha = 0.4F),
    val animationSpec: AnimationSpec<Float> = tween(300),
    val bottomSheetContainer: @Composable (
        modifier: Modifier,
        content: @Composable () -> Unit
    ) -> Unit = { modifier, content ->
        Box(modifier = modifier) {
            content()
        }
    }
)

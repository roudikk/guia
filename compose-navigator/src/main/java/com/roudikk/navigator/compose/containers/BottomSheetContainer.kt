package com.roudikk.navigator.compose.containers

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.snap
import androidx.compose.animation.fadeOut
import androidx.compose.animation.with
import androidx.compose.foundation.layout.Box
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
import androidx.compose.runtime.derivedStateOf
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
import com.roudikk.navigator.compose.BottomSheetSetup
import com.roudikk.navigator.compose.ProvideNavigationVisibilityScope
import com.roudikk.navigator.compose.backstack.BackStackEntry
import com.roudikk.navigator.core.BottomSheet
import com.roudikk.navigator.core.Navigator
import com.roudikk.navigator.extensions.popBackstack

@Composable
@ExperimentalMaterialApi
internal fun rememberBottomSheetState(
    initialValue: ModalBottomSheetValue,
    animationSpec: AnimationSpec<Float> = SwipeableDefaults.AnimationSpec,
    skipHalfExpanded: Boolean,
    confirmStateChange: (ModalBottomSheetValue) -> Boolean = { true }
): ModalBottomSheetState {
    return remember(
        initialValue,
        animationSpec,
        skipHalfExpanded,
        confirmStateChange,
    ) {
        ModalBottomSheetState(
            initialValue = initialValue,
            animationSpec = animationSpec,
            isSkipHalfExpanded = skipHalfExpanded,
            confirmStateChange = confirmStateChange
        )
    }
}

@OptIn(
    ExperimentalMaterialApi::class,
    ExperimentalAnimationApi::class,
)
@Composable
internal fun Navigator.BottomSheetContainer(
    content: @Composable (BackStackEntry) -> Unit,
    bottomSheetEntry: BackStackEntry?,
    bottomSheetSetup: BottomSheetSetup,
    container: @Composable () -> Unit
) {
    val localDensity = LocalDensity.current

    val destination by remember { derivedStateOf { destinations.last() } }
    val navigationNode = bottomSheetEntry?.destination?.let(::navigationNode) as? BottomSheet
    val confirmStateChange by remember(destination) {
        derivedStateOf {
            { sheetValue: ModalBottomSheetValue ->
                val node = navigationNode(destination)
                node !is BottomSheet || node.bottomSheetOptions.confirmStateChange(sheetValue)
            }
        }
    }

    val bottomSheetState = rememberBottomSheetState(
        initialValue = if (bottomSheetEntry == null) {
            ModalBottomSheetValue.Hidden
        } else {
            ModalBottomSheetValue.Expanded
        },
        skipHalfExpanded = bottomSheetSetup.skipHalfExpanded,
        animationSpec = bottomSheetSetup.animationSpec,
        confirmStateChange = confirmStateChange
    )

    var contentHeightPixels by remember(bottomSheetEntry) {
        mutableStateOf(with(localDensity) { 1.dp.toPx() })
    }

    val contentHeightDp by remember(contentHeightPixels) {
        derivedStateOf { with(localDensity) { contentHeightPixels.toDp() } }
    }

    LaunchedEffect(bottomSheetEntry) {
        if (bottomSheetEntry != null) {
            bottomSheetState.show()
        } else {
            bottomSheetState.hide()
        }
    }

    LaunchedEffect(bottomSheetState.currentValue) {
        if (bottomSheetEntry != null && bottomSheetState.currentValue == ModalBottomSheetValue.Hidden) {
            popBackstack()
        }
    }

    ModalBottomSheetLayout(
        sheetState = bottomSheetState,
        modifier = Modifier.fillMaxSize(),
        sheetBackgroundColor = Color.Transparent,
        sheetContentColor = Color.Transparent,
        sheetElevation = 0.dp,
        scrimColor = bottomSheetSetup.scrimColor,
        sheetShape = RoundedCornerShape(0.dp),
        sheetContent = {
            Box(
                modifier = Modifier
                    .fillMaxWidth(),
                contentAlignment = Alignment.BottomCenter
            ) {
                bottomSheetSetup.bottomSheetContainer(
                    modifier = bottomSheetEntry?.destination
                        ?.let {
                            (navigationNode as BottomSheet).bottomSheetOptions.modifier
                        } ?: Modifier
                ) {
                    AnimatedContent(
                        modifier = Modifier
                            .fillMaxWidth(),
                        targetState = bottomSheetEntry,
                        transitionSpec = {
                            // Only animate bottom sheet content when navigating between
                            // bottom sheet destinations.
                            if (navigationNode(destination) !is BottomSheet && targetState != null) {
                                EnterTransition.None
                            } else {
                                transition.enter
                            } with if (initialState != null && navigationNode !is BottomSheet) {
                                fadeOut(animationSpec = snap(delayMillis = 300))
                            } else {
                                transition.exit
                            }
                        }
                    ) { bottomSheetEntry ->
                        if (bottomSheetEntry != null) {
                            Box(
                                modifier = Modifier
                                    .testTag("BottomSheetContainer")
                                    .onGloballyPositioned {
                                        contentHeightPixels = it.size.height.toFloat()
                                    },
                                contentAlignment = Alignment.BottomCenter
                            ) {
                                ProvideNavigationVisibilityScope {
                                    content(bottomSheetEntry)
                                }
                            }
                        } else {
                            Box(modifier = Modifier.height(contentHeightDp))
                        }
                    }
                }
            }
        }
    ) {
        container()
    }
}

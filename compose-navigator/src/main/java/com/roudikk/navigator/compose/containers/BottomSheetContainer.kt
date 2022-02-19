package com.roudikk.navigator.compose.containers

import androidx.compose.animation.*
import androidx.compose.animation.core.snap
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.roudikk.navigator.animation.NavEnterExitTransition
import com.roudikk.navigator.compose.BottomSheetOptions
import com.roudikk.navigator.compose.backstack.BackStackEntry
import com.roudikk.navigator.core.BottomSheet
import com.roudikk.navigator.core.Destination

@OptIn(
    ExperimentalMaterialApi::class,
    ExperimentalAnimationApi::class
)
@Composable
internal fun BottomSheetContainer(
    bottomSheetEntry: BackStackEntry?,
    bottomSheetOptions: BottomSheetOptions,
    transition: NavEnterExitTransition,
    currentDestination: () -> Destination,
    onSheetHidden: () -> Unit,
    content: @Composable AnimatedVisibilityScope.(BackStackEntry) -> Unit
) {
    val confirmStateChange = { sheetValue: ModalBottomSheetValue ->
        val destination = currentDestination()
        destination.navigationNode !is BottomSheet ||
                destination.navigationNode.bottomSheetOptions.confirmStateChange(sheetValue)
    }

    val bottomSheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        animationSpec = bottomSheetOptions.animationSpec,
        confirmStateChange = confirmStateChange
    )

    ModalBottomSheetLayout(
        modifier = Modifier
            .testTag("NavContainerBottomSheet")
            .fillMaxSize(),
        sheetState = bottomSheetState,
        scrimColor = bottomSheetOptions.scrimColor,
        sheetShape = RoundedCornerShape(0.dp),
        sheetBackgroundColor = Color.Transparent,
        sheetElevation = 0.dp,
        sheetContent = {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                val localDensity = LocalDensity.current

                var contentHeightPixels by remember {
                    mutableStateOf(
                        with(localDensity) {
                            1.dp.toPx()
                        }
                    )
                }
                val contentHeightDp = with(localDensity) { contentHeightPixels.toDp() }

                bottomSheetOptions.bottomSheetContainer(
                    modifier = bottomSheetEntry?.destination
                        ?.let {
                            (it.navigationNode as BottomSheet).bottomSheetOptions.modifier
                        } ?: Modifier
                ) {
                    AnimatedContent(
                        modifier = Modifier.fillMaxWidth(),
                        targetState = bottomSheetEntry,
                        transitionSpec = {
                            // Only animate bottom sheet content when navigating between
                            // bottom sheet destinations.
                            val destination = currentDestination()

                            if (destination.navigationNode !is BottomSheet && targetState != null) {
                                EnterTransition.None
                            } else {
                                transition.enter.toComposeEnterTransition()
                            } with if (initialState != null && destination.navigationNode !is BottomSheet) {
                                fadeOut(animationSpec = snap(delayMillis = 300))
                            } else {
                                transition.exit.toComposeExitTransition()
                            }
                        }
                    ) { bottomSheetEntry ->
                        if (bottomSheetEntry != null) {
                            Box(
                                modifier = Modifier
                                    .onGloballyPositioned {
                                        contentHeightPixels = it.size.height.toFloat()
                                    }
                            ) {
                                content(bottomSheetEntry)
                            }
                        } else {
                            Box(modifier = Modifier.height(contentHeightDp))
                        }
                    }
                }
            }
        }
    ) {
        Box(modifier = Modifier.fillMaxSize())
    }

    LaunchedEffect(bottomSheetEntry) {
        if (bottomSheetEntry != null) {
            bottomSheetState.animateTo(ModalBottomSheetValue.Expanded)
        } else {
            bottomSheetState.hide()
        }
    }

    LaunchedEffect(bottomSheetState.isVisible) {
        if (!bottomSheetState.isVisible && bottomSheetEntry != null) {
            onSheetHidden()
        }
    }
}

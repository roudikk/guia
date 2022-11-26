package com.roudikk.navigator.compose.containers

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.snap
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.with
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.BottomSheetScaffold
import androidx.compose.material.BottomSheetValue
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.rememberBottomSheetScaffoldState
import androidx.compose.material.rememberBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.roudikk.navigator.Navigator
import com.roudikk.navigator.compose.BottomSheetSetup
import com.roudikk.navigator.compose.animation.EnterExitTransition
import com.roudikk.navigator.compose.backstack.BackStackEntry
import com.roudikk.navigator.core.BottomSheet
import com.roudikk.navigator.core.Destination

@OptIn(
    ExperimentalMaterialApi::class,
    ExperimentalAnimationApi::class,
)
@Composable
internal fun Navigator.BottomSheetContainer(
    content: @Composable AnimatedVisibilityScope.(BackStackEntry) -> Unit,
    bottomSheetEntry: BackStackEntry?,
    bottomSheetSetup: BottomSheetSetup,
    transition: EnterExitTransition,
    currentDestination: () -> Destination,
    onSheetHidden: () -> Unit,
    container: @Composable () -> Unit
) {
    val navigationNode = bottomSheetEntry?.destination?.let(::navigationNode)
    val confirmStateChange = { sheetValue: BottomSheetValue ->
        val destination = currentDestination()
        val node = navigationNode(destination)
         node !is BottomSheet || node.bottomSheetOptions.confirmStateChange(sheetValue)
    }

    val scaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = rememberBottomSheetState(
            initialValue = BottomSheetValue.Collapsed,
            animationSpec = bottomSheetSetup.animationSpec,
            confirmStateChange = confirmStateChange
        )
    )
    val localDensity = LocalDensity.current

    var contentHeightPixels by remember(bottomSheetEntry) {
        mutableStateOf(with(localDensity) { 0.dp.toPx() })
    }
    val contentHeightDp = with(localDensity) { contentHeightPixels.toDp() }

    LaunchedEffect(bottomSheetEntry) {
        if (bottomSheetEntry != null) {
            scaffoldState.bottomSheetState.expand()
        } else {
            scaffoldState.bottomSheetState.collapse()
        }
    }

    BottomSheetScaffold(
        modifier = Modifier
            .fillMaxSize(),
        scaffoldState = scaffoldState,
        sheetShape = RoundedCornerShape(0.dp),
        sheetBackgroundColor = Color.Transparent,
        sheetElevation = 0.dp,
        backgroundColor = Color.Transparent,
        sheetPeekHeight = 0.dp,
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
                        modifier = Modifier.fillMaxWidth(),
                        targetState = bottomSheetEntry,
                        transitionSpec = {
                            // Only animate bottom sheet content when navigating between
                            // bottom sheet destinations.
                            val destination = currentDestination()

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
        Box(modifier = Modifier.fillMaxSize()) {
            container()

            AnimatedVisibility(
                visible = bottomSheetEntry != null,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Box(
                    modifier = Modifier
                        .testTag("BottomSheetContainer")
                        .pointerInput(Unit) {
                            detectTapGestures(
                                onPress = {
                                    if (confirmStateChange(BottomSheetValue.Collapsed)) {
                                        scaffoldState.bottomSheetState.collapse()
                                    }
                                }
                            ) {}
                        }
                        .fillMaxSize()
                        .background(bottomSheetSetup.scrimColor)
                )
            }
        }
    }

    LaunchedEffect(scaffoldState.bottomSheetState.currentValue) {
        if (scaffoldState.bottomSheetState.isCollapsed && bottomSheetEntry != null) {
            onSheetHidden()
        }
    }
}

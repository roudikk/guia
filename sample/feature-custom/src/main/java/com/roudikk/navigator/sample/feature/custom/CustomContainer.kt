package com.roudikk.navigator.sample.feature.custom

import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.rememberSwipeableState
import androidx.compose.material.swipeable
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.roudikk.navigator.backstack.id
import com.roudikk.navigator.containers.NavigationEntryContainer
import com.roudikk.navigator.core.Navigator
import kotlin.math.abs

fun Navigator.popFirst() {
    setBackstack(backStack.drop(1))
}

enum class CardState {
    IDLE,
    SWIPED_LEFT,
    SWIPED_RIGHT
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
internal fun Navigator.CustomContainer(
    modifier: Modifier = Modifier
) {
    val customBackStackManager = rememberCustomBackStackManager(navigator = this)

    customBackStackManager.visibleBackStack.entries.forEach { entry ->
        key(entry.id) {
            BoxWithConstraints(
                modifier = modifier,
                contentAlignment = Alignment.Center
            ) {
                val swipeableState = rememberSwipeableState(initialValue = CardState.IDLE)
                val widthPx = with(LocalDensity.current) { (maxWidth + 16.dp).toPx() }
                val offset = swipeableState.offset.value.toInt()
                val alphaMaxWidth = 0.8F * widthPx

                Card(
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .width(maxWidth - 32.dp)
                        .height(maxHeight - 32.dp)
                        .offset { IntOffset(x = offset, y = 0) }
                        .rotate((offset * 25 / widthPx))
                        .alpha(1.2F - abs(offset / alphaMaxWidth))
                        .swipeable(
                            state = swipeableState,
                            anchors = mapOf(
                                -widthPx to CardState.SWIPED_LEFT,
                                0F to CardState.IDLE,
                                widthPx to CardState.SWIPED_RIGHT
                            ),
                            orientation = Orientation.Horizontal
                        )
                ) {
                    NavigationEntryContainer(
                        backStackManager = customBackStackManager,
                        lifecycleEntry = entry
                    )

                    LaunchedEffect(swipeableState.currentValue) {
                        if (swipeableState.currentValue != CardState.IDLE) {
                            popFirst()
                        }
                    }
                }
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose(customBackStackManager::onDispose)
    }
}

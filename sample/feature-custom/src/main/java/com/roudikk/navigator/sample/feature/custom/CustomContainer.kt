package com.roudikk.navigator.sample.feature.custom

import android.util.Log
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.rememberSwipeableState
import androidx.compose.material.swipeable
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.roudikk.navigator.core.Navigator
import com.roudikk.navigator.core.navigationNode
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
) = Box(modifier = modifier) {
    val saveableStateHolder = rememberSaveableStateHolder()

    backStack.reversed().forEach { backStackEntry ->
        saveableStateHolder.SaveableStateProvider(backStackEntry.id) {
            BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                val swipeableState = rememberSwipeableState(initialValue = CardState.IDLE)
                val widthPx = with(LocalDensity.current) { (maxWidth + 16.dp).toPx() }
                val offset = swipeableState.offset.value.toInt()
                val alphaMaxWidth = 0.8F * widthPx

                Card(
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .swipeable(
                            state = swipeableState,
                            anchors = mapOf(
                                -widthPx to CardState.SWIPED_LEFT,
                                0F to CardState.IDLE,
                                widthPx to CardState.SWIPED_RIGHT
                            ),
                            orientation = Orientation.Horizontal
                        )
                        .alpha(1F - abs(offset / alphaMaxWidth))
                        .rotate((offset * 45 / widthPx))
                        .offset {
                            IntOffset(x = offset, y = 0)
                        }
                ) {
                    navigationNode(backStackEntry).content()
                }

                LaunchedEffect(swipeableState.currentValue) {
                    if (swipeableState.currentValue != CardState.IDLE) {
                        Log.d("TEST", "Pop first")
                        popFirst()
                    }
                }
            }
        }
    }
}

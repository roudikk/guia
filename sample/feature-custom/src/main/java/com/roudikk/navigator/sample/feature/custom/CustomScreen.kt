package com.roudikk.navigator.sample.feature.custom

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import kotlinx.coroutines.delay

private val colorSaver = Saver<Color, List<Float>>(
    save = {
        listOf(it.red, it.green, it.blue, it.alpha)
    },
    restore = {
        Color(
            red = it[0],
            green = it[1],
            blue = it[2],
            alpha = it[3]
        )
    }
)

@Composable
fun CustomScreen(id: Int) {
    val backgroundColor = rememberSaveable(saver = colorSaver) {
        Color(
            red = 255 - (77..99).random(),
            green = 255 - (77..99).random(),
            blue = 255 - (77..99).random(),
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor),
        contentAlignment = Alignment.Center
    ) {
        var timer by rememberSaveable { mutableStateOf(0) }
        val lifecycle = LocalLifecycleOwner.current.lifecycle
        var currentState by remember { mutableStateOf<Lifecycle.State?>(null) }

        DisposableEffect(Unit) {
            val observer = LifecycleEventObserver { _, event ->
                currentState = event.targetState
            }

            lifecycle.addObserver(observer)
            onDispose { lifecycle.removeObserver(observer) }
        }

        LaunchedEffect(currentState) {
            if (currentState == Lifecycle.State.RESUMED) {
                while (true) {
                    delay(1000)
                    timer++
                }
            }
        }

        Text(
            text = "$id: $timer",
            fontSize = 20.sp,
            color = Color.White,
            fontWeight = FontWeight.Bold
        )
    }
}

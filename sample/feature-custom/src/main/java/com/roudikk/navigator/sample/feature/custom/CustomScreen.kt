package com.roudikk.navigator.sample.feature.custom

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
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
            red = (0..155).random(),
            green = (0..155).random(),
            blue = (0..155).random(),
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor),
        contentAlignment = Alignment.Center
    ) {
        var timer by rememberSaveable { mutableStateOf(0) }

        LaunchedEffect(Unit) {
            while (true) {
                delay(1000)
                timer++
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

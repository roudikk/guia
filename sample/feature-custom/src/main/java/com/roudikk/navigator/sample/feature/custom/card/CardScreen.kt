package com.roudikk.navigator.sample.feature.custom.card

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.material.TextButton
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.viewmodel.compose.viewModel
import com.roudikk.navigator.sample.feature.common.deeplink.GlobalNavigator
import com.roudikk.navigator.sample.feature.common.navigation.LocalNavHostViewModelStoreOwner
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
fun CardScreen(id: Int) {
    val globalNavigator = viewModel<GlobalNavigator>(LocalNavHostViewModelStoreOwner.current)
    val backgroundColor = rememberSaveable(saver = colorSaver) {
        Color(
            red = 255 - (88..111).random(),
            green = 255 - (88..111).random(),
            blue = 255 - (88..111).random(),
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .border(8.dp, Color.Black.copy(alpha = 0.2F), RoundedCornerShape(32.dp)),
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

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "Card $id: $timer",
                fontSize = 30.sp,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "(Timer will only start when resumed)",
                fontSize = 14.sp,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(8.dp))

            TextButton(
                colors = ButtonDefaults.textButtonColors(
                    contentColor = Color.White,
                    backgroundColor = Color.Black.copy(alpha = 0.4F)
                ),
                onClick = {
                    globalNavigator.navigateToDetails(item = id.toString())
                }
            ) {
                Text(text = "Open Details")
            }
        }
    }
}

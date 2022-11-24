package com.roudikk.navigator.sample.ui.screens.nested

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.roudikk.navigator.NavigationKey
import com.roudikk.navigator.Navigator
import com.roudikk.navigator.NavigatorRulesScope
import com.roudikk.navigator.canGoBack
import com.roudikk.navigator.compose.requireNavigator
import com.roudikk.navigator.navigate
import com.roudikk.navigator.popBackStack
import com.roudikk.navigator.sample.ui.theme.AppTheme
import kotlinx.parcelize.Parcelize

@Parcelize
data class NestedKey(val count: Int) : NavigationKey

fun NavigatorRulesScope.nestedNavigation() {
    screen<NestedKey> { NestedScreen(count = it.count) }
}

@Composable
private fun NestedScreen(
    navigator: Navigator = requireNavigator(),
    count: Int
) {
    val canGoBack by navigator.canGoBack()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        IconButton(
            enabled = canGoBack,
            onClick = {
                navigator.popBackStack()
            }
        ) {
            Icon(
                imageVector = Icons.Default.Remove,
                contentDescription = "Remove"
            )
        }

        Surface(
            tonalElevation = 10.dp,
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier.size(100.dp)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                Text(
                    text = "$count",
                    style = MaterialTheme.typography.headlineLarge
                )
            }
        }

        IconButton(
            onClick = {
                navigator.navigate(NestedKey(count + 1))
            }
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add"
            )
        }
    }
}

@Preview(
    device = Devices.PIXEL_3
)
@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    device = Devices.PIXEL_3
)
@Composable
private fun NestedContentPreview() = AppTheme {
//    NestedScreen(
//        navigator = rememberNavigator(SampleNavConfig.Nested),
//        count = 4
//    )
}

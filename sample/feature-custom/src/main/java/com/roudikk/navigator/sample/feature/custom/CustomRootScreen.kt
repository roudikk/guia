package com.roudikk.navigator.sample.feature.custom

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.roudikk.navigator.core.entries
import com.roudikk.navigator.core.rememberNavigator
import com.roudikk.navigator.sample.feature.custom.api.CustomKey

@Composable
fun CustomRootScreen() {
    val navigator = rememberNavigator(
        initialKey = CustomKey(0),
        builder = { customNavigation() },
        initialize = { it.setBackstack((0..20).map { CustomKey(it) }.entries()) }
    )

    navigator.CustomContainer(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    )
}

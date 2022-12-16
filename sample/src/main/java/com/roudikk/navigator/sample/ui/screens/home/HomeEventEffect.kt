package com.roudikk.navigator.sample.ui.screens.home

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import com.roudikk.navigator.compose.requireNavigator
import com.roudikk.navigator.extensions.clearResult
import com.roudikk.navigator.extensions.navigate
import com.roudikk.navigator.extensions.pushResult
import com.roudikk.navigator.sample.navigation.requireRootNavigator
import com.roudikk.navigator.sample.ui.screens.details.DetailsKey
import com.roudikk.navigator.sample.ui.screens.details.DetailsResult
import com.roudikk.navigator.sample.ui.screens.settings.SettingsKey

@Composable
fun HomeEventEffect(
    viewModel: HomeViewModel
) {
    val navigator = requireNavigator()
    val rootNavigator = requireRootNavigator()
    val context = LocalContext.current
    val event = viewModel.event

    LaunchedEffect(event) {
        when (event) {
            is HomeEvent.ShowToast -> context.showToast(event.item)
            is HomeEvent.OpenDetails -> navigator.navigate(DetailsKey(event.item))
            is HomeEvent.RefreshResult -> navigator.pushResult(DetailsResult(event.item))
            is HomeEvent.OpenSettings -> rootNavigator.navigate(SettingsKey())
            is HomeEvent.ClearResult -> navigator.clearResult<DetailsResult>()
            else -> return@LaunchedEffect
        }

        viewModel.onEventHandled()
    }
}

private fun Context.showToast(text: String) {
    Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
}

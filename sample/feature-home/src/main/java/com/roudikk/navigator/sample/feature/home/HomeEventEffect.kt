package com.roudikk.navigator.sample.feature.home

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import com.roudikk.navigator.extensions.clearResult
import com.roudikk.navigator.extensions.push
import com.roudikk.navigator.extensions.setResult
import com.roudikk.navigator.extensions.requireLocalNavigator
import com.roudikk.navigator.sample.feature.common.navigation.requireRootNavigator
import com.roudikk.navigator.sample.feature.details.api.DetailsKey
import com.roudikk.navigator.sample.feature.details.api.DetailsResult
import com.roudikk.navigator.sample.feature.settings.api.SettingsKey

@Composable
fun HomeEventEffect(
    viewModel: HomeViewModel
) {
    val navigator = requireLocalNavigator()
    val rootNavigator = requireRootNavigator()
    val context = LocalContext.current
    val event = viewModel.event

    LaunchedEffect(event) {
        when (event) {
            is HomeEvent.ShowToast -> context.showToast(event.item)
            is HomeEvent.OpenDetails -> navigator.push(DetailsKey(event.item))
            is HomeEvent.RefreshResult -> navigator.setResult(DetailsResult(event.item))
            is HomeEvent.OpenSettings -> rootNavigator.push(SettingsKey())
            is HomeEvent.ClearResult -> navigator.clearResult<DetailsResult>()
            else -> return@LaunchedEffect
        }

        viewModel.onEventHandled()
    }
}

private fun Context.showToast(text: String) {
    Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
}

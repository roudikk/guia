package com.roudikk.guia.sample.feature.home

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import com.roudikk.guia.extensions.LocalNavigator
import com.roudikk.guia.extensions.clearResult
import com.roudikk.guia.extensions.currentOrThrow
import com.roudikk.guia.extensions.push
import com.roudikk.guia.extensions.setResult
import com.roudikk.guia.sample.feature.common.navigation.LocalRootNavigator
import com.roudikk.guia.sample.feature.details.navigation.DetailsKey
import com.roudikk.guia.sample.feature.details.navigation.DetailsResult
import com.roudikk.guia.sample.feature.settings.navigation.SettingsKey

@Composable
fun HomeEventEffect(
    viewModel: HomeViewModel
) {
    val navigator = LocalNavigator.currentOrThrow
    val rootNavigator = LocalRootNavigator.current
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

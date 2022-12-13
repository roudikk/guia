package com.roudikk.navigator.sample.ui.screens.details

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.roudikk.navigator.compose.requireNavigator
import com.roudikk.navigator.core.Navigator
import com.roudikk.navigator.extensions.navigate
import com.roudikk.navigator.extensions.popBackstack
import com.roudikk.navigator.extensions.popToRoot
import com.roudikk.navigator.extensions.pushResult
import com.roudikk.navigator.extensions.replaceLast
import com.roudikk.navigator.extensions.singleInstance
import com.roudikk.navigator.extensions.singleTop

@Composable
fun DetailsEventEffect(
    viewModel: DetailsViewModel
) {
    val navigator = requireNavigator()
    val event = viewModel.event

    LaunchedEffect(event) {
        when (event) {
            DetailsEvent.GoBack -> navigator.popBackstack()

            is DetailsEvent.OpenBottomSheet -> navigator.navigate(
                navigationKey = DetailsBottomSheetKey(event.item)
            )

            is DetailsEvent.OpenExistingSingleInstance -> navigator.singleInstance(
                navigationKey = DetailsKey(event.item),
                useExistingInstance = true
            )

            is DetailsEvent.OpenNewSingleInstance -> navigator.singleInstance(
                navigationKey = DetailsKey(event.item),
                useExistingInstance = false
            )

            is DetailsEvent.OpenRandomItem -> navigator.navigate(
                navigationKey = DetailsKey(event.item),
            )

            is DetailsEvent.OpenDynamicItem -> navigator.navigate(
                navigationKey = DynamicDetailsKey(event.item)
            )

            is DetailsEvent.OpenReplaced -> navigator.replaceLast(
                navigationKey = DetailsKey(event.item),
            )

            is DetailsEvent.OpenSingleTop -> navigator.singleTop(
                navigationKey = DetailsKey(event.item),
            )

            is DetailsEvent.OpenSingleTopBottomSheet -> navigator.singleTop(
                navigationKey = DetailsBottomSheetKey(event.item)
            )

            is DetailsEvent.OpenDialog -> navigator.navigate(
                navigationKey = DetailsDialogKey(event.item)
            )

            is DetailsEvent.SendResult -> {
                navigator.pushResult(DetailsResult(event.result))
                navigator.popToRoot()
            }

            else -> return@LaunchedEffect
        }

        viewModel.onEventHandled()
    }
}

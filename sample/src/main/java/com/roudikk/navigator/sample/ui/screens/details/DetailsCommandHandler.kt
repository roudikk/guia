package com.roudikk.navigator.sample.ui.screens.details

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
    navigator: Navigator,
    viewModel: DetailsViewModel
) {
    val command = viewModel.event
    LaunchedEffect(command) {
        when (command) {
            DetailsEvent.GoBack -> navigator.popBackstack()

            is DetailsEvent.OpenBottomSheet -> navigator.navigate(
                navigationKey = DetailsBottomSheetKey(command.item)
            )

            is DetailsEvent.OpenExistingSingleInstance -> navigator.singleInstance(
                navigationKey = DetailsKey(command.item),
                useExistingInstance = true
            )

            is DetailsEvent.OpenNewSingleInstance -> navigator.singleInstance(
                navigationKey = DetailsKey(command.item),
                useExistingInstance = false
            )

            is DetailsEvent.OpenRandomItem -> navigator.navigate(
                navigationKey = DetailsKey(command.item),
            )

            is DetailsEvent.OpenDynamicItem -> navigator.navigate(
                navigationKey = DynamicDetailsKey(command.item)
            )

            is DetailsEvent.OpenReplaced -> navigator.replaceLast(
                navigationKey = DetailsKey(command.item),
            )

            is DetailsEvent.OpenSingleTop -> navigator.singleTop(
                navigationKey = DetailsKey(command.item),
            )

            is DetailsEvent.OpenSingleTopBottomSheet -> navigator.singleTop(
                navigationKey = DetailsBottomSheetKey(command.item)
            )

            is DetailsEvent.OpenDialog -> navigator.navigate(
                navigationKey = DetailsDialogKey(command.item)
            )

            is DetailsEvent.SendResult -> {
                navigator.pushResult(DetailsResult(command.result))
                navigator.popToRoot()
            }

            else -> return@LaunchedEffect
        }
        viewModel.onEventHandled()
    }
}

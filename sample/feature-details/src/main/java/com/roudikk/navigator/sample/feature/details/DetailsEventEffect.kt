package com.roudikk.navigator.sample.feature.details

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.roudikk.navigator.extensions.navigate
import com.roudikk.navigator.extensions.popBackstack
import com.roudikk.navigator.extensions.popToRoot
import com.roudikk.navigator.extensions.replaceLast
import com.roudikk.navigator.extensions.requireNavigator
import com.roudikk.navigator.extensions.requireParentNavigator
import com.roudikk.navigator.extensions.setResult
import com.roudikk.navigator.extensions.singleInstance
import com.roudikk.navigator.extensions.singleTop
import com.roudikk.navigator.sample.feature.common.navigation.CrossFadeTransition
import com.roudikk.navigator.sample.feature.details.api.DetailsKey
import com.roudikk.navigator.sample.feature.details.api.DetailsResult
import com.roudikk.navigator.sample.feature.dialogs.api.BlockingBottomSheetKey

@Composable
fun DetailsEventEffect(
    viewModel: DetailsViewModel
) {
    val navigator = requireNavigator()
    val parentNavigator = requireParentNavigator()
    val event = viewModel.event

    LaunchedEffect(event) {
        when (event) {
            DetailsEvent.GoBack -> navigator.popBackstack()

            is DetailsEvent.OpenBottomSheet -> navigator.navigate(
                navigationKey = DetailsBottomSheetKey(event.item)
            )

            is DetailsEvent.OpenExistingSingleInstance -> navigator.singleInstance(
                navigationKey = DetailsKey(event.item),
                useExisting = true
            )

            is DetailsEvent.OpenNewSingleInstance -> navigator.singleInstance(
                navigationKey = DetailsKey(event.item),
                useExisting = false
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

            is DetailsEvent.OpenBlockingBottomSheet -> parentNavigator.navigate(
                navigationKey = BlockingBottomSheetKey()
            )

            is DetailsEvent.OverrideScreenTransition -> {
                navigator.overrideNextTransition = CrossFadeTransition.enterExit
                navigator.navigate(DetailsKey(event.item))
            }

            is DetailsEvent.SendResult -> {
                navigator.setResult(DetailsResult(event.result))
                navigator.popToRoot()
            }

            else -> return@LaunchedEffect
        }

        viewModel.onEventHandled()
    }
}

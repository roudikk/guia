package com.roudikk.navigator.sample.feature.details

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.roudikk.navigator.extensions.navigate
import com.roudikk.navigator.extensions.popBackstack
import com.roudikk.navigator.extensions.popToRoot
import com.roudikk.navigator.extensions.replaceLast
import com.roudikk.navigator.extensions.requireLocalNavigator
import com.roudikk.navigator.extensions.requireLocalParentNavigator
import com.roudikk.navigator.extensions.setResult
import com.roudikk.navigator.extensions.singleInstance
import com.roudikk.navigator.extensions.singleTop
import com.roudikk.navigator.sample.feature.common.navigation.CrossFadeTransition
import com.roudikk.navigator.sample.feature.details.api.DetailsKey
import com.roudikk.navigator.sample.feature.details.api.DetailsResult
import com.roudikk.navigator.sample.feature.dialogs.api.BlockingBottomSheetKey

@Suppress("CyclomaticComplexMethod")
@Composable
fun DetailsEventEffect(
    viewModel: DetailsViewModel
) {
    val navigator = requireLocalNavigator()
    val parentNavigator = requireLocalParentNavigator()
    val event = viewModel.event

    LaunchedEffect(event) {
        when (event) {
            DetailsEvent.GoBack -> navigator.popBackstack()

            is DetailsEvent.OpenBottomSheet -> navigator.navigate(
                navigationKey = DetailsBottomSheetKey(event.item)
            )

            is DetailsEvent.OpenExistingSingleInstance -> navigator.singleInstance(
                navigationKey = DetailsKey(event.item),
                checkForExisting = true
            )

            is DetailsEvent.OpenNewSingleInstance -> navigator.singleInstance(
                navigationKey = DetailsKey(event.item),
                checkForExisting = false
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
                navigator.overrideScreenTransition = CrossFadeTransition.enterExit
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

package com.roudikk.navigator.sample.feature.details

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.roudikk.navigator.core.Screen
import com.roudikk.navigator.core.overrideTransition
import com.roudikk.navigator.extensions.push
import com.roudikk.navigator.extensions.pop
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
            DetailsEvent.GoBack -> navigator.pop()

            is DetailsEvent.OpenBottomSheet -> navigator.push(
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

            is DetailsEvent.OpenRandomItem -> navigator.push(
                navigationKey = DetailsKey(event.item),
            )

            is DetailsEvent.OpenDynamicItem -> navigator.push(
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

            is DetailsEvent.OpenDialog -> navigator.push(
                navigationKey = DetailsDialogKey(event.item)
            )

            is DetailsEvent.OpenBlockingBottomSheet -> parentNavigator.push(
                navigationKey = BlockingBottomSheetKey()
            )

            is DetailsEvent.OverrideScreenTransition -> {
                navigator.overrideTransition<Screen>(CrossFadeTransition.enterExit)
                navigator.push(DetailsKey(event.item))
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

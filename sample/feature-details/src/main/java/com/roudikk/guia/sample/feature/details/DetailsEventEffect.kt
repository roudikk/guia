package com.roudikk.guia.sample.feature.details

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.roudikk.guia.core.Screen
import com.roudikk.guia.core.overrideTransition
import com.roudikk.guia.extensions.push
import com.roudikk.guia.extensions.pop
import com.roudikk.guia.extensions.popToRoot
import com.roudikk.guia.extensions.replaceLast
import com.roudikk.guia.extensions.requireLocalNavigator
import com.roudikk.guia.extensions.requireLocalParentNavigator
import com.roudikk.guia.extensions.setResult
import com.roudikk.guia.extensions.singleInstance
import com.roudikk.guia.extensions.singleTop
import com.roudikk.guia.sample.feature.common.navigation.CrossFadeTransition
import com.roudikk.guia.sample.feature.details.navigation.DetailsCustomTransitionKey
import com.roudikk.guia.sample.feature.details.navigation.DetailsKey
import com.roudikk.guia.sample.feature.details.navigation.DetailsResult
import com.roudikk.guia.sample.feature.dialogs.navigation.BlockingBottomSheetKey

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

            is DetailsEvent.CustomScreenTransition -> {
                navigator.push(DetailsCustomTransitionKey(event.item))
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

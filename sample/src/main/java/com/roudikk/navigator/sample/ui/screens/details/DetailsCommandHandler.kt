package com.roudikk.navigator.sample.ui.screens.details

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.roudikk.navigator.core.Navigator
import com.roudikk.navigator.extensions.navigate
import com.roudikk.navigator.extensions.popBackstack
import com.roudikk.navigator.extensions.popToRoot
import com.roudikk.navigator.extensions.replaceLast
import com.roudikk.navigator.extensions.singleInstance
import com.roudikk.navigator.extensions.singleTop
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@Composable
fun DetailsCommandHandler(
    navigator: Navigator,
    viewModel: DetailsViewModel
) {
    LaunchedEffect(Unit) {
        viewModel.commandsFlow
            .onEach { command ->
                when (command) {
                    DetailsCommand.GoBack -> navigator.popBackstack()
                    is DetailsCommand.OpenBottomSheet -> navigator.navigate(
                        navigationKey = DetailsBottomSheetKey(command.item)
                    )

                    is DetailsCommand.OpenExistingSingleInstance -> navigator.singleInstance(
                        navigationKey = DetailsKey(command.item),
                        useExistingInstance = true
                    )

                    is DetailsCommand.OpenNewSingleInstance -> navigator.singleInstance(
                        navigationKey = DetailsKey(command.item),
                        useExistingInstance = false
                    )

                    is DetailsCommand.OpenRandomItem -> navigator.navigate(
                        navigationKey = DetailsKey(command.item),
                    )

                    is DetailsCommand.OpenDynamicItem -> navigator.navigate(
                        navigationKey = DynamicDetailsKey(command.item)
                    )

                    is DetailsCommand.OpenReplaced -> navigator.replaceLast(
                        navigationKey = DetailsKey(command.item),
                    )

                    is DetailsCommand.OpenSingleTop -> navigator.singleTop(
                        navigationKey = DetailsKey(command.item),
                    )

                    is DetailsCommand.OpenSingleTopBottomSheet -> navigator.singleTop(
                        navigationKey = DetailsBottomSheetKey(command.item)
                    )

                    is DetailsCommand.OpenDialog -> navigator.navigate(
                        navigationKey = DetailsDialogKey(command.item)
                    )

                    is DetailsCommand.SendResult -> {
//                        navigator.sendResult<HomeScreen>(command.result)
                        navigator.popToRoot()
                    }
                }
            }
            .launchIn(this)
    }
}

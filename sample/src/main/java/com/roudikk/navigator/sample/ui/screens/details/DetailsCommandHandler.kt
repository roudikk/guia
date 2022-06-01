package com.roudikk.navigator.sample.ui.screens.details

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.roudikk.navigator.Navigator
import com.roudikk.navigator.core.asBottomSheet
import com.roudikk.navigator.core.asDialog
import com.roudikk.navigator.core.asScreen
import com.roudikk.navigator.sample.navigation.CrossFadeTransition
import com.roudikk.navigator.sample.ui.screens.home.HomeScreen
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
                    DetailsCommand.GoBack -> navigator.popBackStack()
                    is DetailsCommand.OpenBottomSheet -> navigator.navigate(
                        navigationNode = DetailsScreen(command.item, false)
                            .asBottomSheet(),
                        transition = CrossFadeTransition
                    )
                    is DetailsCommand.OpenExistingSingleInstance -> navigator.singleInstance(
                        navigationNode = DetailsScreen(command.item)
                            .asScreen(),
                        useExistingInstance = true
                    )
                    is DetailsCommand.OpenNewSingleInstance -> navigator.singleInstance(
                        navigationNode = DetailsScreen(command.item)
                            .asScreen(),
                        useExistingInstance = false
                    )
                    is DetailsCommand.OpenRandomItem -> navigator.navigate(
                        navigationNode = DetailsScreen(command.item)
                            .asScreen(),
                    )
                    is DetailsCommand.OpenReplaced -> navigator.replaceLast(
                        navigationNode = DetailsScreen(command.item)
                            .asScreen(),
                    )
                    is DetailsCommand.OpenSingleTop -> navigator.singleTop(
                        navigationNode = DetailsScreen(command.item)
                            .asScreen(),
                    )
                    is DetailsCommand.OpenSingleTopBottomSheet -> navigator.singleTop(
                        navigationNode = DetailsScreen(command.item, false)
                            .asBottomSheet(),
                        transition = CrossFadeTransition
                    )
                    is DetailsCommand.OpenDialog -> navigator.navigate(
                        navigationNode = DetailsScreen(command.item, false)
                            .asDialog(),
                    )
                    is DetailsCommand.SendResult -> {
                        navigator.sendResult<HomeScreen>(command.result)
                        navigator.popToRoot()
                    }
                }
            }
            .launchIn(this)
    }
}

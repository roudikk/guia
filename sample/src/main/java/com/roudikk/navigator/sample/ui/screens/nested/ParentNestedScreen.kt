package com.roudikk.navigator.sample.ui.screens.nested

import android.content.res.Configuration
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.insets.imePadding
import com.roudikk.navigator.Navigator
import com.roudikk.navigator.compose.NavContainer
import com.roudikk.navigator.core.Screen
import com.roudikk.navigator.rememberNavigator
import com.roudikk.navigator.sample.DeepLinkViewModel
import com.roudikk.navigator.sample.NestedDestination
import com.roudikk.navigator.sample.navigation.LocalNavHostViewModelStoreOwner
import com.roudikk.navigator.sample.navigation.SampleNavConfig
import com.roudikk.navigator.sample.ui.composables.AppTopAppBar
import com.roudikk.navigator.sample.ui.theme.AppTheme
import kotlinx.parcelize.Parcelize

@Parcelize
class ParentNestedScreen : Screen {

    @Composable
    override fun Content() {
        val mainViewModel = viewModel<DeepLinkViewModel>(
            viewModelStoreOwner = LocalNavHostViewModelStoreOwner.current
        )
        val nestedNavigator = rememberNavigator(SampleNavConfig.Nested) { navigator ->
            navigator.deeplink(mainViewModel.nestedDestinations)
        }

        LaunchedEffect(Unit) {
            mainViewModel.nestedDestinationsFlow.collect { destinations ->
                nestedNavigator.deeplink(destinations)
            }
        }

        ParentNestedContent(nestedNavigator)
    }

    private fun Navigator.deeplink(destinations: List<NestedDestination>) {
        destinations.forEach { destination ->
            when (destination) {
                is NestedDestination.Nested -> navigate(NestedScreen(destination.count))
            }
        }
    }
}

@Composable
private fun ParentNestedContent(nestedNavigator: Navigator) {
    Scaffold(
        topBar = {
            AppTopAppBar(title = "Nested Navigation")
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Box(modifier = Modifier.weight(1f)) {
                NavContainer(navigator = nestedNavigator)
            }

            Row(
                modifier = Modifier
                    .imePadding()
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 16.dp)
                    .height(52.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Button(
                    modifier = Modifier
                        .padding(end = 8.dp)
                        .fillMaxHeight()
                        .weight(1f),
                    onClick = {
                        nestedNavigator.popToRoot()
                    }
                ) {
                    Text(
                        modifier = Modifier,
                        text = "Pop to root"
                    )
                }

                val textFieldValue = rememberSaveable { mutableStateOf("") }

                OutlinedTextField(
                    modifier = Modifier
                        .padding(start = 8.dp)
                        .weight(1f),
                    value = textFieldValue.value,
                    onValueChange = {
                        textFieldValue.value = it
                    },
                    shape = RoundedCornerShape(20.dp),
                    trailingIcon = {
                        IconButton(onClick = {
                            textFieldValue.value.toIntOrNull()?.let {
                                nestedNavigator.popTo("NestedScreen_$it")
                            }
                        }) {
                            Icon(
                                imageVector = Icons.Default.ArrowForward,
                                contentDescription = "Pop"
                            )
                        }
                    },
                    placeholder = {
                        Text(text = "Pop to index")
                    },
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        textColor = MaterialTheme.colorScheme.onSurface
                    ),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number
                    )
                )
            }
        }
    }
}

@Preview(
    device = Devices.PIXEL_3
)
@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    device = Devices.PIXEL_3
)
@Composable
private fun NestedContentPreview() = AppTheme {
    ParentNestedContent(rememberNavigator(SampleNavConfig.Nested))
}

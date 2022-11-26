package com.roudikk.navigator.sample.ui.screens.nested

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.roudikk.navigator.NavigationKey
import com.roudikk.navigator.NavigatorRulesScope
import com.roudikk.navigator.compose.NavContainer
import com.roudikk.navigator.popTo
import com.roudikk.navigator.popToRoot
import com.roudikk.navigator.rememberNavigator
import com.roudikk.navigator.sample.navigation.VerticalSlideTransition
import com.roudikk.navigator.sample.ui.composables.AppTopAppBar
import com.roudikk.navigator.sample.ui.theme.AppTheme
import kotlinx.parcelize.Parcelize

@Parcelize
class ParentNestedKey : NavigationKey

fun NavigatorRulesScope.parentNestedNavigation() {
    screen<ParentNestedKey> { ParentNestedScreen() }
}

@Composable
fun ParentNestedScreen() {
    val nestedNavigator = rememberNavigator(NestedKey(1)) {
        defaultTransition { _, _ -> VerticalSlideTransition }
        nestedNavigation()
    }

    ParentNestedContent(
        onPopToRootClicked = nestedNavigator::popToRoot,
        onPopToClicked = {
            nestedNavigator.popTo<NestedKey> { key ->
                key.count == it
            }
        }
    ) {
        nestedNavigator.NavContainer()
    }
}

@Composable
private fun ParentNestedContent(
    onPopToRootClicked: () -> Unit = {},
    onPopToClicked: (Int) -> Unit = {},
    container: @Composable () -> Unit
) {
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
                container()
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
                    onClick = onPopToRootClicked
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
                                onPopToClicked(it)
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
    ParentNestedContent {}
}

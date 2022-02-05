package com.roudikk.navigator.sample.ui.screens.nested

import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.accompanist.insets.imePadding
import com.roudikk.navigator.*
import com.roudikk.navigator.sample.AppNavHost
import com.roudikk.navigator.sample.AppNavigator
import com.roudikk.navigator.sample.ui.composables.AppTopAppBar
import com.roudikk.navigator.sample.ui.theme.AppTheme
import kotlinx.parcelize.Parcelize

@Parcelize
class ParentNestedScreen : Screen {

    @Composable
    override fun AnimatedVisibilityScope.Content() {
        ParentNestedContent()
    }
}

@Composable
private fun ParentNestedContent(
    nestedNavigator: Navigator = findNavigator(AppNavigator.NestedTab.key)
) {
    Scaffold(
        topBar = {
            AppTopAppBar(title = "Nested Navigation")
        }
    ) {

        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Box(modifier = Modifier.weight(1f)) {
                NavContainer(
                    modifier = Modifier.fillMaxSize(),
                    key = AppNavigator.NestedTab.key
                )
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
    AppNavHost {
        ParentNestedContent()
    }
}

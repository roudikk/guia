package com.roudikk.navigator.core

import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.BottomSheetValue
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.SecureFlagPolicy

/**
 * Represents a navigation node in the navigation tree.
 */
sealed interface NavigationNode {

    @Composable
    fun Content()
}

/**
 * A screen representation of a [NavigationNode].
 */
interface Screen : NavigationNode

/**
 * A Dialog representation of a [NavigationNode].
 *
 * @property dialogOptions, extra dialog options.
 */
interface Dialog : NavigationNode {

    val dialogOptions: DialogOptions
        get() = DialogOptions()
}

/**
 * A Bottom sheet representation of a [NavigationNode].
 *
 * @property bottomSheetOptions, extra bottom sheet options.
 */
interface BottomSheet : NavigationNode {

    val bottomSheetOptions: BottomSheetOptions
        get() = BottomSheetOptions()
}

/**
 * Options used in a [Dialog].
 *
 * @property dismissOnClickOutside, whether to dismiss the dialog when clicking outside
 * its bounds.
 * @property dismissOnBackPress, whether to dismiss the dialog when pressing the back
 * button.
 */
data class DialogOptions(
    val modifier: Modifier = Modifier.widthIn(max = 300.dp),
    val dismissOnClickOutside: Boolean = true,
    val dismissOnBackPress: Boolean = true,
    val securePolicy: SecureFlagPolicy = SecureFlagPolicy.Inherit,
)

@OptIn(ExperimentalComposeUiApi::class)
fun DialogOptions.toDialogProperties() = DialogProperties(
    dismissOnBackPress = dismissOnBackPress,
    dismissOnClickOutside = dismissOnClickOutside,
    securePolicy = securePolicy,
    usePlatformDefaultWidth = false
)

/**
 * Options used in a [BottomSheet]
 *
 * @property modifier, modifier applied to the bottom sheet container
 * @property confirmStateChange, check [rememberModalBottomSheetState]
 * it reaches a hidden state.
 */
@OptIn(ExperimentalMaterialApi::class)
data class BottomSheetOptions(
    val modifier: Modifier = Modifier,
    val confirmStateChange: (state: BottomSheetValue) -> Boolean = { true }
)

fun screenNode(content: @Composable () -> Unit) = object : Screen {

    @Composable
    override fun Content() = content()
}

fun dialogNode(
    dialogOptions: DialogOptions = DialogOptions(),
    content: @Composable () -> Unit
) = object : Dialog {

    override val dialogOptions: DialogOptions
        get() = dialogOptions

    @Composable
    override fun Content() = content()
}

fun bottomSheetNode(
    bottomSheetOptions: BottomSheetOptions = BottomSheetOptions(),
    content: @Composable () -> Unit
) = object : BottomSheet {

    override val bottomSheetOptions: BottomSheetOptions
        get() = bottomSheetOptions

    @Composable
    override fun Content() = content()
}

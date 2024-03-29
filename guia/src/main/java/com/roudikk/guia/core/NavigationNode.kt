package com.roudikk.guia.core

import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.SecureFlagPolicy
import com.roudikk.guia.containers.BottomSheetValue
import com.roudikk.guia.core.Dialog.DialogOptions

/**
 * Represents how a navigation key will be displayed in the navigation tree.
 */
interface NavigationNode {
    val content: @Composable () -> Unit
}

/**
 * A screen representation of a [NavigationKey].
 */
class Screen(
    override val content: @Composable () -> Unit
) : NavigationNode

/**
 * A Dialog representation of a [NavigationKey].
 *
 * @property dialogOptions, extra dialog options.
 */
class Dialog(
    override val content: @Composable () -> Unit
) : NavigationNode {

    constructor(
        dialogOptions: DialogOptions,
        content: @Composable () -> Unit
    ) : this(content) {
        this.dialogOptions = dialogOptions
    }

    var dialogOptions by mutableStateOf(DialogOptions())

    /**
     * Options used in a [Dialog].
     *
     * @property dismissOnClickOutside, whether to dismiss the dialog when clicking outside
     * its bounds.
     * @property dismissOnBackPress, whether to dismiss the dialog when pressing the back
     * button.
     * @property secureFlagPolicy, @see [SecureFlagPolicy]
     */
    data class DialogOptions(
        val dismissOnClickOutside: Boolean = true,
        val dismissOnBackPress: Boolean = true,
        val secureFlagPolicy: SecureFlagPolicy = SecureFlagPolicy.Inherit,
    )
}

/**
 * Converts a [DialogOptions] to [DialogProperties].
 */
fun DialogOptions.toDialogProperties() = DialogProperties(
    dismissOnBackPress = dismissOnBackPress,
    dismissOnClickOutside = dismissOnClickOutside,
    securePolicy = secureFlagPolicy,
    usePlatformDefaultWidth = false
)

/**
 * A Bottom sheet representation of a [NavigationKey].
 *
 * @property bottomSheetOptions, extra bottom sheet options.
 */
class BottomSheet(
    override val content: @Composable () -> Unit
) : NavigationNode {

    constructor(
        bottomSheetOptions: BottomSheetOptions,
        content: @Composable () -> Unit
    ) : this(content) {
        this.bottomSheetOptions = bottomSheetOptions
    }

    var bottomSheetOptions by mutableStateOf(BottomSheetOptions())

    /**
     * Options used in a [BottomSheet].
     *
     * @property confirmStateChange, check [rememberModalBottomSheetState]
     * it reaches a hidden state.
     */
    data class BottomSheetOptions(
        val scrimColor: Color? = null,
        val confirmStateChange: (value: BottomSheetValue) -> Boolean = { true },
        val dismissOnClickOutside: Boolean = true,
        val onOutsideClick: () -> Unit = {}
    )
}

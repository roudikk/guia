package com.roudikk.navigator.core

import android.os.Parcelable
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.SecureFlagPolicy
import com.roudikk.navigator.Navigator
import kotlinx.parcelize.Parcelize

/**
 * Represents a navigation node in the navigation tree.
 */
sealed interface NavigationNode : Parcelable {

    /**
     * The key for a navigation node, make sure this is unique across nodes or the navigator might
     * run into unexpected behaviours. By default, this uses the class name as a key, this means
     * when creating an instance using object: NavigationNode for ex, the key will be empty, in
     * that case make sure to override key with something more meaningful.
     */
    val key: String
        get() = this::class.java.name

    /**
     * The key for listening to results passed to this navigation node.
     */
    val resultsKey: String
        get() = "${key}_Results"

    /**
     * Composable UI of the navigation node.
     */
    @Composable
    fun Content()

    companion object {
        inline fun <reified T : NavigationNode> key(): String = T::class.java.name
        inline fun <reified T : NavigationNode> resultsKey() = "${key<T>()}_Results"
    }

    /**
     * Convenience function to listen to results in navigation node without having to pass
     * results key.
     */
    fun Navigator.nodeResults() = results(resultsKey)
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
 * Returns the [NavigationNode] as a [BottomSheet].
 * The [NavigationNode] must implement [BottomSheet].
 */
fun NavigationNode.asBottomSheet(): BottomSheet {
    require(this is BottomSheet) { "NavigationNode: $this is not a BottomSheet, make sure it implements a BottomSheet" }
    return object : BottomSheet by this {}
}

/**
 * Returns the [NavigationNode] as a [Screen].
 * The [NavigationNode] must implement [Screen].
 */
fun NavigationNode.asScreen(): Screen {
    require(this is Screen) { "NavigationNode: $this is not a Screen, make sure it implements a Screen" }
    return object : Screen by this {}
}

/**
 * Returns the [NavigationNode] as a [Dialog].
 * The [NavigationNode] must implement [Dialog].
 */
fun NavigationNode.asDialog(): Dialog {
    require(this is Dialog) { "NavigationNode: $this is not a Dialog, make sure it implements a Dialog" }
    return object : Dialog by this {}
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
    val confirmStateChange: (state: ModalBottomSheetValue) -> Boolean = { true }
)

/**
 * Use this when there is no starting destination and the navigator requires an empty initial node.
 */
@Parcelize
object EmptyNavigationNode : NavigationNode {

    @Composable
    override fun Content() {
        // No Content
    }
}

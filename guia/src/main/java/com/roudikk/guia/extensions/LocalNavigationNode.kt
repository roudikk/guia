package com.roudikk.guia.extensions

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.compositionLocalOf
import com.roudikk.guia.core.BottomSheet
import com.roudikk.guia.core.Dialog
import com.roudikk.guia.core.NavigationNode
import com.roudikk.guia.core.Navigator
import com.roudikk.guia.core.Screen

/**
 * The local navigation node that is hosting a certain composable.
 */
val LocalNavigationNode = compositionLocalOf<NavigationNode?> { null }

/**
 * Returns the current local [NavigationNode].
 *
 * @throws IllegalStateException if called inside a Composable not hosted by a [NavigationNode]
 */
val ProvidableCompositionLocal<NavigationNode?>.currentOrThrow: NavigationNode
    @ReadOnlyComposable
    @Composable
    inline get() = checkNotNull(current) {
        "LocalNavigationNode must be called inside a Composable hosted in a navigation node."
    }

/**
 * Returns the current local [NavigationNode] if one is available.
 */
@Deprecated(
    "Use LocalNavigationNode.current",
    replaceWith = ReplaceWith(
        expression = "LocalNavigationNode.current",
        imports = arrayOf("com.roudikk.guia.extensions.LocalNavigationNode")
    )
)
@Composable
fun localNavigationNode(): NavigationNode? = LocalNavigationNode.current

/**
 * Returns the current local [NavigationNode].
 *
 * @throws IllegalStateException if called inside a Composable not hosted by a [NavigationNode]
 */
@Deprecated(
    "Use LocalNavigationNode.currentOrThrow",
    replaceWith = ReplaceWith(
        expression = "LocalNavigationNode.currentOrThrow",
        imports = arrayOf(
            "com.roudikk.guia.extensions.LocalNavigationNode",
            "com.roudikk.guia.extensions.currentOrThrow",
        )
    )
)
@Composable
fun requireLocalNavigationNode(): NavigationNode {
    return checkNotNull(LocalNavigationNode.current) {
        "Must be called inside a Composable hosted in a navigation node."
    }
}

/**
 * Returns the current local [BottomSheet] if one is available.
 */
val ProvidableCompositionLocal<NavigationNode?>.currentAsBottomSheet: BottomSheet?
    @ReadOnlyComposable
    @Composable
    inline get() = current as? BottomSheet

/**
 * Returns the current local [BottomSheet].
 *
 * @throws IllegalStateException if the navigation node is not a bottom sheet.
 */
val ProvidableCompositionLocal<NavigationNode?>.currentAsBottomSheetOrThrow: BottomSheet
    @ReadOnlyComposable
    @Composable
    inline get() = checkNotNull(currentAsBottomSheet) {
        "Must be called in a Composable hosted in a BottomSheet"
    }

/**
 * Returns the current local [BottomSheet] if one is available.
 */
@Deprecated(
    "Use LocalNavigationNode.currentAsBottomSheet",
    replaceWith = ReplaceWith(
        expression = "LocalNavigationNode.currentAsBottomSheet",
        imports = arrayOf(
            "com.roudikk.guia.extensions.LocalNavigationNode",
            "com.roudikk.guia.extensions.currentAsBottomSheet",
        )
    )
)
@Composable
fun localBottomSheet() = localNavigationNode() as? BottomSheet

/**
 * Returns the current local [BottomSheet].
 *
 * @throws IllegalStateException if the navigation node is not a bottom sheet.
 */
@Deprecated(
    "Use LocalNavigationNode.currentAsBottomSheetOrThrow",
    replaceWith = ReplaceWith(
        expression = "LocalNavigationNode.currentAsBottomSheetOrThrow",
        imports = arrayOf(
            "com.roudikk.guia.extensions.LocalNavigationNode",
            "com.roudikk.guia.extensions.currentAsBottomSheetOrThrow",
        )
    )
)
@Composable
fun requireLocalBottomSheet() = checkNotNull(localBottomSheet()) {
    "Must be called in a Composable hosted in a BottomSheet"
}

/**
 * Returns the current local [Dialog] if one is available.
 */
val ProvidableCompositionLocal<NavigationNode?>.currentAsDialog: Dialog?
    @ReadOnlyComposable
    @Composable
    inline get() = current as? Dialog

/**
 * Returns the current local [Dialog].
 *
 * @throws IllegalStateException if the navigation node is not a dialog.
 */
val ProvidableCompositionLocal<NavigationNode?>.currentAsDialogOrThrow: Dialog
    @ReadOnlyComposable
    @Composable
    inline get() = checkNotNull(currentAsDialog) {
        "Must be called in a Composable hosted in a Dialog"
    }

/**
 * Returns the current local [Dialog] if one is available.
 */
@Deprecated(
    "Use LocalNavigationNode.currentAsDialog",
    replaceWith = ReplaceWith(
        expression = "LocalNavigationNode.currentAsDialog",
        imports = arrayOf(
            "com.roudikk.guia.extensions.LocalNavigationNode",
            "com.roudikk.guia.extensions.currentAsDialog",
        )
    )
)
@Composable
fun localDialog() = localNavigationNode() as? Dialog

/**
 * Returns the current local [Dialog].
 *
 * @throws IllegalStateException if the navigation node is not a dialog.
 */
@Deprecated(
    "Use LocalNavigationNode.currentAsDialogOrThrow",
    replaceWith = ReplaceWith(
        expression = "LocalNavigationNode.currentAsDialogOrThrow",
        imports = arrayOf(
            "com.roudikk.guia.extensions.LocalNavigationNode",
            "com.roudikk.guia.extensions.currentAsDialogOrThrow",
        )
    )
)
@Composable
fun requireLocalDialog() = checkNotNull(localDialog()) {
    "Must be called in a Composable hosted in a Dialog"
}

/**
 * Returns the current local [Screen] if one is available.
 */
val ProvidableCompositionLocal<NavigationNode?>.currentAsScreen: Screen?
    @ReadOnlyComposable
    @Composable
    inline get() = current as? Screen

/**
 * Returns the current local [Screen].
 *
 * @throws IllegalStateException if the navigation node is not a screen.
 */
val ProvidableCompositionLocal<NavigationNode?>.currentAsScreenOrThrow: Screen
    @ReadOnlyComposable
    @Composable
    inline get() = checkNotNull(currentAsScreen) {
        "Must be called in a Composable hosted in a screen"
    }


/**
 * Returns the current local [Screen] if one is available.
 */
@Deprecated(
    "Use LocalNavigationNode.currentAsScreen",
    replaceWith = ReplaceWith(
        expression = "LocalNavigationNode.currentAsScreen",
        imports = arrayOf(
            "com.roudikk.guia.extensions.LocalNavigationNode",
            "com.roudikk.guia.extensions.currentAsScreen",
        )
    )
)
@Composable
fun localScreen() = localNavigationNode() as? Screen

/**
 * Returns the current local [Screen].
 *
 * @throws IllegalStateException if the navigation node is not a screen.
 */
@Deprecated(
    "Use LocalNavigationNode.currentAsScreenOrThrow",
    replaceWith = ReplaceWith(
        expression = "LocalNavigationNode.currentAsScreenOrThrow",
        imports = arrayOf(
            "com.roudikk.guia.extensions.LocalNavigationNode",
            "com.roudikk.guia.extensions.currentAsScreenOrThrow",
        )
    )
)
@Composable
fun requireLocalScreen() = checkNotNull(localScreen()) {
    "Must be called in a Composable hosted in a screen"
}

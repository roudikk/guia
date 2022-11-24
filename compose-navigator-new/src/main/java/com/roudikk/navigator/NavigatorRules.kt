package com.roudikk.navigator

import androidx.compose.runtime.Composable
import com.roudikk.navigator.core.BottomSheet
import com.roudikk.navigator.core.BottomSheetOptions
import com.roudikk.navigator.core.Dialog
import com.roudikk.navigator.core.DialogOptions
import com.roudikk.navigator.core.NavigationNode
import com.roudikk.navigator.core.Screen
import kotlin.reflect.KClass

class NavigatorRules(
    val associations: HashMap<KClass<NavigationKey>, (NavigationKey) -> NavigationNode> = hashMapOf()
)

@Suppress("UNCHECKED_CAST")
class NavigatorRulesScope {

    @PublishedApi
    internal val associations: HashMap<KClass<NavigationKey>, (NavigationKey) -> NavigationNode> =
        hashMapOf()

    inline fun <reified Key : NavigationKey> navigationNode(
        noinline navigationNodeBuilder: (Key) -> NavigationNode
    ) {
        associations[Key::class as KClass<NavigationKey>] =
            navigationNodeBuilder as (NavigationKey) -> NavigationNode
    }

    inline fun <reified Key : NavigationKey> screen(noinline content: @Composable (Key) -> Unit) {
        navigationNode<Key> {
            screenNode {
                content(it)
            }
        }
    }

    inline fun <reified Key : NavigationKey> dialog(
        dialogOptions: DialogOptions = DialogOptions(),
        noinline content: @Composable (Key) -> Unit
    ) {
        navigationNode<Key> {
            dialogNode(dialogOptions) {
                content(it)
            }
        }
    }

    inline fun <reified Key : NavigationKey> bottomSheet(
        bottomSheetOptions: BottomSheetOptions = BottomSheetOptions(),
        noinline content: @Composable (Key) -> Unit
    ) {
        navigationNode<Key> {
            bottomSheetNode(bottomSheetOptions) {
                content(it)
            }
        }
    }

    fun build() = NavigatorRules(
        associations = associations
    )
}

fun NavigatorRulesScope.screenNode(content: @Composable () -> Unit) = object : Screen {

    @Composable
    override fun Content() = content()
}

fun NavigatorRulesScope.dialogNode(
    dialogOptions: DialogOptions = DialogOptions(),
    content: @Composable () -> Unit
) = object : Dialog {
    override val dialogOptions: DialogOptions
        get() = dialogOptions

    @Composable
    override fun Content() = content()
}

fun NavigatorRulesScope.bottomSheetNode(
    bottomSheetOptions: BottomSheetOptions = BottomSheetOptions(),
    content: @Composable () -> Unit
) = object : BottomSheet {
    override val bottomSheetOptions: BottomSheetOptions
        get() = bottomSheetOptions

    @Composable
    override fun Content() = content()
}

package com.roudikk.guia.sample.feature.details

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import com.roudikk.guia.core.BottomSheet
import com.roudikk.guia.core.Dialog
import com.roudikk.guia.core.NavigationKey
import com.roudikk.guia.core.NavigatorConfigBuilder
import com.roudikk.guia.extensions.LocalNavigationNode
import com.roudikk.guia.extensions.LocalNavigator
import com.roudikk.guia.extensions.currentAsBottomSheet
import com.roudikk.guia.extensions.currentOrThrow
import com.roudikk.guia.extensions.pop
import com.roudikk.guia.sample.feature.common.navigation.CrossFadeTransition
import com.roudikk.guia.sample.feature.common.navigation.VerticalSlideTransition
import com.roudikk.guia.sample.feature.details.navigation.DetailsCustomTransitionKey
import com.roudikk.guia.sample.feature.details.navigation.DetailsKey
import kotlinx.parcelize.Parcelize

@Parcelize
internal class DetailsDialogKey(val item: String) : NavigationKey.WithNode<Dialog> {
    override fun navigationNode() = Dialog {
        DetailsContent(item = item)
    }
}

private val colorSaver = Saver<Color, List<Float>>(
    save = {
        listOf(it.red, it.green, it.blue, it.alpha)
    },
    restore = {
        Color(
            red = it[0],
            green = it[1],
            blue = it[2],
            alpha = it[3]
        )
    }
)

@Parcelize
class DetailsBottomSheetKey(val item: String) : NavigationKey.WithNode<BottomSheet> {
    override fun navigationNode() = BottomSheet {
        val navigator = LocalNavigator.currentOrThrow
        val bottomSheet = LocalNavigationNode.currentAsBottomSheet
        val scrimColor = rememberSaveable(key = "color", saver = colorSaver) {
            Color(
                red = (0..255).random(),
                green = (0..255).random(),
                blue = (0..255).random(),
            ).copy(alpha = 0.12F)
        }

        LaunchedEffect(LocalConfiguration.current) {
            bottomSheet ?: return@LaunchedEffect
            bottomSheet.bottomSheetOptions = bottomSheet.bottomSheetOptions.copy(
                scrimColor = scrimColor,
                dismissOnClickOutside = false,
                onOutsideClick = { navigator.pop() }
            )
        }

        DetailsContent(item = item)
    }
}

@Parcelize
internal class DynamicDetailsKey(val item: String) : NavigationKey

fun NavigatorConfigBuilder.detailsNavigation(screenWidth: Int) {
    if (screenWidth <= 600) {
        dialog<DynamicDetailsKey> {
            DetailsContent(item = it.item)
        }
    } else {
        bottomSheet<DynamicDetailsKey> {
            DetailsContent(item = it.item)
        }
    }

    screen<DetailsKey> { DetailsScaffold(item = it.item) }
    screen<DetailsCustomTransitionKey> { DetailsScaffold(item = it.item) }

    keyTransition<DynamicDetailsKey> { -> CrossFadeTransition }
    keyTransition<DetailsCustomTransitionKey> { -> VerticalSlideTransition }
}

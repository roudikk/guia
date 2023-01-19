package com.roudikk.navigator.sample.feature.details

import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import com.roudikk.navigator.core.BottomSheet
import com.roudikk.navigator.core.Dialog
import com.roudikk.navigator.core.Dialog.DialogOptions
import com.roudikk.navigator.core.NavigationKey
import com.roudikk.navigator.core.NavigatorConfigBuilder
import com.roudikk.navigator.extensions.localBottomSheet
import com.roudikk.navigator.extensions.popBackstack
import com.roudikk.navigator.extensions.requireLocalNavigator
import com.roudikk.navigator.sample.feature.common.composables.SampleSurfaceContainer
import com.roudikk.navigator.sample.feature.common.navigation.CrossFadeTransition
import com.roudikk.navigator.sample.feature.common.navigation.MaterialSharedAxisTransitionX
import com.roudikk.navigator.sample.feature.common.navigation.VerticalSlideTransition
import com.roudikk.navigator.sample.feature.details.api.DetailsKey
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
        val navigator = requireLocalNavigator()
        val bottomSheet = localBottomSheet()
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
                onOutsideClick = { navigator.popBackstack() }
            )
        }

        DetailsContent(item = item)
    }
}

@Parcelize
internal class DynamicDetailsKey(val item: String) : NavigationKey

fun NavigatorConfigBuilder.detailsNavigation(screenWidth: Int) {
    if (screenWidth <= 600) {
        dialog<DynamicDetailsKey>(
            dialogOptions = DialogOptions(modifier = Modifier.widthIn(max = 300.dp))
        ) {
            SampleSurfaceContainer { DetailsContent(item = it.item) }
        }
    } else {
        bottomSheet<DynamicDetailsKey> {
            DetailsContent(item = it.item)
        }
    }

    screen<DetailsKey> { DetailsScaffold(item = it.item) }

    transition<DetailsBottomSheetKey> { -> CrossFadeTransition }
    transition<DetailsDialogKey> { -> VerticalSlideTransition }
    transition<DynamicDetailsKey> { -> CrossFadeTransition }
    transition<DetailsKey> { -> MaterialSharedAxisTransitionX }
}

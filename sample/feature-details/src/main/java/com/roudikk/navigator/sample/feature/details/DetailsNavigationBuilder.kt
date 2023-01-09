package com.roudikk.navigator.sample.feature.details

import androidx.compose.foundation.layout.widthIn
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.roudikk.navigator.core.BottomSheet
import com.roudikk.navigator.core.Dialog
import com.roudikk.navigator.core.Dialog.DialogOptions
import com.roudikk.navigator.core.NavigationKey
import com.roudikk.navigator.core.NavigatorConfigBuilder
import com.roudikk.navigator.sample.feature.common.composables.SampleSurfaceContainer
import com.roudikk.navigator.sample.feature.common.navigation.CrossFadeTransition
import com.roudikk.navigator.sample.feature.common.navigation.MaterialSharedAxisTransitionX
import com.roudikk.navigator.sample.feature.details.api.DetailsKey
import kotlinx.parcelize.Parcelize

@Parcelize
internal class DetailsDialogKey(val item: String) : NavigationKey.WithNode<Dialog> {
    override fun navigationNode() = Dialog {
        SampleSurfaceContainer { DetailsContent(item = item) }
    }
}

@Parcelize
class DetailsBottomSheetKey(val item: String) : NavigationKey.WithNode<BottomSheet> {
    override fun navigationNode() = BottomSheet(
        bottomSheetOptions = BottomSheet.BottomSheetOptions(
            scrimColor = Color(
                red = (0..255).random(),
                green = (0..255).random(),
                blue = (0..255).random(),
            ).copy(alpha = 0.12F)
        )
    ) {
        DetailsContent(item = item)
    }
}

@Parcelize
internal class DynamicDetailsKey(val item: String) : NavigationKey

fun NavigatorConfigBuilder.detailsNavigation(screenWidth: Int) {
    if (screenWidth <= 600) {
        dialog<DynamicDetailsKey>(
            dialogOptions = DialogOptions(modifier = Modifier.widthIn(max = 320.dp))
        ) {
            SampleSurfaceContainer { DetailsContent(item = it.item) }
        }
    } else {
        bottomSheet<DynamicDetailsKey> {
            DetailsContent(item = it.item)
        }
    }

    screen<DetailsKey> { DetailsScaffold(item = it.item) }

    transition<DetailsBottomSheetKey> { -> MaterialSharedAxisTransitionX }
    transition<DetailsDialogKey> { -> MaterialSharedAxisTransitionX }
    transition<DynamicDetailsKey> { -> CrossFadeTransition }
    transition<DetailsKey> { -> MaterialSharedAxisTransitionX }
}

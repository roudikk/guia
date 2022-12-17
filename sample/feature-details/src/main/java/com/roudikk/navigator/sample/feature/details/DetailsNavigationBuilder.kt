package com.roudikk.navigator.sample.feature.details

import androidx.compose.foundation.layout.widthIn
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.roudikk.navigator.NavigatorBuilderScope
import com.roudikk.navigator.core.BottomSheet
import com.roudikk.navigator.core.Dialog
import com.roudikk.navigator.core.DialogOptions
import com.roudikk.navigator.core.NavigationKey
import com.roudikk.navigator.core.bottomSheetNode
import com.roudikk.navigator.core.dialogNode
import com.roudikk.navigator.sample.feature.common.composables.SampleSurfaceContainer
import com.roudikk.navigator.sample.feature.common.navigation.CrossFadeTransition
import com.roudikk.navigator.sample.feature.common.navigation.MaterialSharedAxisTransitionX
import com.roudikk.navigator.sample.feature.details.api.DetailsKey
import kotlinx.parcelize.Parcelize

@Parcelize
internal class DetailsDialogKey(val item: String) : NavigationKey.WithNode<Dialog> {
    override fun navigationNode() = dialogNode {
        SampleSurfaceContainer { DetailsContent(item = item) }
    }
}

@Parcelize
internal class DetailsBottomSheetKey(val item: String) : NavigationKey.WithNode<BottomSheet> {
    override fun navigationNode() = bottomSheetNode {
        DetailsContent(item = item)
    }
}

@Parcelize
internal class DynamicDetailsKey(val item: String) : NavigationKey

fun NavigatorBuilderScope.detailsNavigation(screenWidth: Int) {
    if (screenWidth <= 600) {
        dialog<DynamicDetailsKey>(
            dialogOptions = DialogOptions(modifier = Modifier.widthIn(max = 320.dp))
        ) {
            SampleSurfaceContainer { DetailsContent(item = it.item) }
        }
    } else {
        bottomSheet<DynamicDetailsKey> { DetailsContent(item = it.item) }
    }

    screen<DetailsKey> { DetailsScaffold(item = it.item) }

    transition<DetailsBottomSheetKey> { -> CrossFadeTransition }
    transition<DetailsDialogKey> { -> CrossFadeTransition }
    transition<DynamicDetailsKey> { -> CrossFadeTransition }
    transition<DetailsKey> { -> MaterialSharedAxisTransitionX }
}

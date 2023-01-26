package com.roudikk.guia.sample.feature.dialogs

import com.roudikk.guia.core.BottomSheet.BottomSheetOptions
import com.roudikk.guia.core.Dialog.DialogOptions
import com.roudikk.guia.core.NavigatorConfigBuilder
import com.roudikk.guia.sample.feature.common.navigation.MaterialSharedAxisTransitionX
import com.roudikk.guia.sample.feature.dialogs.api.BlockingBottomSheetKey
import com.roudikk.guia.sample.feature.dialogs.api.BlockingDialogKey
import com.roudikk.guia.sample.feature.dialogs.api.CancelableDialogKey
import com.roudikk.guia.sample.feature.dialogs.api.DialogsKey

fun NavigatorConfigBuilder.dialogsNavigation() {
    bottomSheet<BlockingBottomSheetKey>(
        bottomSheetOptions = BottomSheetOptions(confirmStateChange = { false })
    ) {
        BlockingBottomSheetScreen()
    }

    dialog<BlockingDialogKey>(
        DialogOptions(dismissOnBackPress = false, dismissOnClickOutside = false)
    ) {
        BlockingDialogScreen(showNextButton = it.showNextButton)
    }

    dialog<CancelableDialogKey> {
        CancelableDialogScreen(showNextButton = it.showNextButton)
    }

    screen<DialogsKey> { DialogsScreen() }

    defaultTransition { previousKey, newKey -> MaterialSharedAxisTransitionX }
}

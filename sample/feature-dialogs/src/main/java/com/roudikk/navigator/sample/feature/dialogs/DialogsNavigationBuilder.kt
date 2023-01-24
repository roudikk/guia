package com.roudikk.navigator.sample.feature.dialogs

import com.roudikk.navigator.core.BottomSheet.BottomSheetOptions
import com.roudikk.navigator.core.Dialog.DialogOptions
import com.roudikk.navigator.core.NavigatorConfigBuilder
import com.roudikk.navigator.sample.feature.common.navigation.MaterialSharedAxisTransitionX
import com.roudikk.navigator.sample.feature.dialogs.api.BlockingBottomSheetKey
import com.roudikk.navigator.sample.feature.dialogs.api.BlockingDialogKey
import com.roudikk.navigator.sample.feature.dialogs.api.CancelableDialogKey
import com.roudikk.navigator.sample.feature.dialogs.api.DialogsKey

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

    defaultTransition { -> MaterialSharedAxisTransitionX }
}

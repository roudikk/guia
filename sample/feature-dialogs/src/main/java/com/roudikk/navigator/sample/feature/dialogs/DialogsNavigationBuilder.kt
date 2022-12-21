package com.roudikk.navigator.sample.feature.dialogs

import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.ui.Modifier
import com.roudikk.navigator.core.NavigatorBuilderScope
import com.roudikk.navigator.core.BottomSheetOptions
import com.roudikk.navigator.core.DialogOptions
import com.roudikk.navigator.sample.feature.dialogs.api.BlockingBottomSheetKey
import com.roudikk.navigator.sample.feature.dialogs.api.BlockingDialogKey
import com.roudikk.navigator.sample.feature.dialogs.api.CancelableDialogKey
import com.roudikk.navigator.sample.feature.dialogs.api.DialogsKey

fun NavigatorBuilderScope.dialogsNavigation() {
    bottomSheet<BlockingBottomSheetKey>(
        bottomSheetOptions = BottomSheetOptions(
            modifier = Modifier.navigationBarsPadding(),
            confirmStateChange = { false },
        )
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
}

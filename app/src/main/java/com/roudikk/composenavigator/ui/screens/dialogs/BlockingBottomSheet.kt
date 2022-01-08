package com.roudikk.composenavigator.ui.screens.dialogs

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.accompanist.insets.navigationBarsPadding
import com.roudikk.compose_navigator.BottomSheet
import com.roudikk.compose_navigator.BottomSheetOptions
import kotlinx.parcelize.Parcelize

@Parcelize
class BlockingBottomSheet : BottomSheet {

    override val bottomSheetOptions: BottomSheetOptions
        get() = BottomSheetOptions(false)

    @Composable
    override fun Content(animatedVisibilityScope: AnimatedVisibilityScope) {
        BlockingBottomSheetScreenContent()
    }
}

@Composable
private fun BlockingBottomSheetScreenContent() {
    var lockBack by rememberSaveable { mutableStateOf(true) }

    Column(Modifier.padding(16.dp)) {
        Text(text = "You can't navigate away by clicking outside this bottom sheet.")

        Spacer(modifier = Modifier.size(16.dp))

        Text(
            text = "Only thing you can do is hit the back button, but that won't go back to the dialogs screen"
                    + " if the below switch is turned on. Toggle it on/off and see what happens!"
        )

        Spacer(modifier = Modifier.size(16.dp))

        Switch(checked = lockBack, onCheckedChange = { lockBack = it })

        Spacer(modifier = Modifier.navigationBarsPadding())
    }

    BackHandler(enabled = lockBack) {
        // Lock back button
    }
}
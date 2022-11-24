package com.roudikk.navigator.sample.navigation

import com.roudikk.navigator.core.StackKey
import kotlinx.parcelize.Parcelize

sealed class SampleStackKey : StackKey {

    @Parcelize
    object Home : SampleStackKey()

    @Parcelize
    object Nested : SampleStackKey()

    @Parcelize
    object Dialogs : SampleStackKey()

    @Parcelize
    object StackTree : SampleStackKey()
}

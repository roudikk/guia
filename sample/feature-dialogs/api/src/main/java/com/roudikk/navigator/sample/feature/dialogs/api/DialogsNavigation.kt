package com.roudikk.navigator.sample.feature.dialogs.api

import com.roudikk.navigator.core.NavigationKey
import com.roudikk.navigator.navhost.StackKey
import kotlinx.parcelize.Parcelize

@Parcelize
class BlockingBottomSheetKey : NavigationKey

@Parcelize
class BlockingDialogKey(val showNextButton: Boolean) : NavigationKey

@Parcelize
class CancelableDialogKey(val showNextButton: Boolean) : NavigationKey

@Parcelize
object DialogsStackKey : StackKey

@Parcelize
class DialogsKey : NavigationKey

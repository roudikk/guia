package com.roudikk.navigator.sample.feature.custom.api

import com.roudikk.navigator.core.NavigationKey
import com.roudikk.navigator.navhost.StackKey
import kotlinx.parcelize.Parcelize

@Parcelize
object CustomStackKey : StackKey

@Parcelize
class CustomRootKey : NavigationKey

@Parcelize
class CustomKey(val id: Int) : NavigationKey

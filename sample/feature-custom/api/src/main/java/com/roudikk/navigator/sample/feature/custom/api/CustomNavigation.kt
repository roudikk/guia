package com.roudikk.navigator.sample.feature.custom.api

import com.roudikk.navigator.core.NavigationKey
import com.roudikk.navigator.navhost.StackKey
import kotlinx.parcelize.Parcelize

@Parcelize
object ViewPagerStackKey : StackKey

@Parcelize
class ViewPagerRootKey : NavigationKey

@Parcelize
class CardKey(val id: Int) : NavigationKey

@Parcelize
class PageKey(val isActive: Boolean) : NavigationKey

package com.roudikk.guia.sample.feature.custom.navigation

import com.roudikk.guia.core.NavigationKey
import com.roudikk.guia.navhost.StackKey
import kotlinx.parcelize.Parcelize

@Parcelize
object CustomStackKey : StackKey

@Parcelize
class ViewPagerRootKey : NavigationKey

@Parcelize
class CardKey(val id: Int) : NavigationKey

@Parcelize
class PageKey(val isActive: Boolean) : NavigationKey

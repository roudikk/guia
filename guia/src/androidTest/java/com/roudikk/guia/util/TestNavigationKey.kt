package com.roudikk.guia.util

import com.roudikk.guia.core.NavigationKey
import kotlinx.parcelize.Parcelize

@Parcelize
class TestNavigationKey : NavigationKey

@Parcelize
class TestKey : NavigationKey

@Parcelize
class TestKey2 : NavigationKey

@Parcelize
class TestKey3 : NavigationKey

@Parcelize
data class TestDataKey(val data: Int) : NavigationKey

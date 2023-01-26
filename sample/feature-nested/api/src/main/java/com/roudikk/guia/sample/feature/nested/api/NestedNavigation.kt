package com.roudikk.guia.sample.feature.nested.api

import com.roudikk.guia.core.NavigationKey
import com.roudikk.guia.navhost.StackKey
import kotlinx.parcelize.Parcelize

@Parcelize
object NestedStackKey : StackKey

@Parcelize
class ParentNestedKey : NavigationKey

@Parcelize
class NestedKey(val index: Int) : NavigationKey {

    override fun tag(): String = tagFor(index)

    companion object {
        fun tagFor(count: Int) = "NestedKey_$count"
    }
}

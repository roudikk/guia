package com.roudikk.navigator

import android.os.Parcelable
import com.roudikk.navigator.core.NavigationNode
import kotlin.reflect.KClass

interface NavigationKey : Parcelable {

    fun tag() = tag(this::class as KClass<NavigationKey>)

    @Suppress("UNCHECKED_CAST")
    companion object {
        inline fun <reified Key : NavigationKey> tag() = tag(Key::class as KClass<NavigationKey>)
        fun tag(navigationKey: KClass<NavigationKey>): String = navigationKey.java.simpleName
    }
}

interface SimpleNavigationKey<Node : NavigationNode> : NavigationKey {

    fun navigationNode(): Node
}

package com.roudikk.navigator.core

import android.os.Parcelable
import kotlin.reflect.KClass

@Suppress("UNCHECKED_CAST")
interface NavigationKey : Parcelable {

    fun tag() = tag(this::class as KClass<NavigationKey>)

    companion object {
        inline fun <reified Key : NavigationKey> tag() = tag(Key::class as KClass<NavigationKey>)
        fun tag(navigationKey: KClass<NavigationKey>): String = navigationKey.java.simpleName
    }
}

interface NavigationNodeKey<Node : NavigationNode> : NavigationKey {

    fun navigationNode(): Node
}

interface ExpectsResult<Result> : NavigationKey

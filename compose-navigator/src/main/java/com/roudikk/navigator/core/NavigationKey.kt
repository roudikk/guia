@file:Suppress("UNCHECKED_CAST")

package com.roudikk.navigator.core

import android.os.Parcelable
import kotlin.reflect.KClass

/**
 * The essential component of a [Navigator]'s backstack.
 *
 * A [NavigationKey] on its own does not have a UI representation. It simply defines a key that will
 * be in the backstack. (Unless it's a [NavigationKey.WithNode], see below)
 *
 * The UI a [NavigationKey] renders is decided using [NavigatorBuilder.presentations].
 *
 * A Navigation Key is [Parcelable] and will be saved and restored during state restoration and
 * process death, so all properties must be primitives or [Parcelable]s.
 */
interface NavigationKey : Parcelable {

    /**
     * A self hosted [NavigationKey] which provides its own UI.
     */
    interface WithNode<Node : NavigationNode> : NavigationKey {

        fun navigationNode(): Node
    }

    /**
     * Tag used for use in UI Testing.
     */
    fun tag() = tag(this::class as KClass<NavigationKey>)

    companion object {
        inline fun <reified Key : NavigationKey> tag() = tag(Key::class as KClass<NavigationKey>)
        fun tag(navigationKey: KClass<NavigationKey>): String = navigationKey.java.simpleName
    }
}

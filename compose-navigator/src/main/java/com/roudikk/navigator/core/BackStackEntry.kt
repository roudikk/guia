package com.roudikk.navigator.core

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.UUID

/**
 * Represents a unique entry in the navigation history.
 *
 * @property navigationKey, navigation node for this entry.
 * @property transition, transition animation.
 * @property id, unique identifier of the entry.
 */
@Parcelize
class BackStackEntry internal constructor(
    val navigationKey: NavigationKey,
    val id: String
) : Parcelable {

    constructor(
        navigationKey: NavigationKey
    ) : this(
        navigationKey = navigationKey,
        id = UUID.randomUUID().toString()
    )

    override fun equals(other: Any?): Boolean {
        return other is BackStackEntry && other.id == id
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
}

fun NavigationKey.entry() = BackStackEntry(this)

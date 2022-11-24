package com.roudikk.navigator.core

import android.os.Parcelable
import com.roudikk.navigator.NavigationKey
import kotlinx.parcelize.Parcelize
import java.util.UUID

/**
 * Represents an entry in the navigation history.
 *
 * @property navigationNode, navigation node for this entry.
 * @property transition, transition animation.
 * @property id, unique identifier of the destination.
 */
@Parcelize
data class Destination internal constructor(
    val navigationKey: NavigationKey,
    val id: String
): Parcelable {

    constructor(
        navigationKey: NavigationKey
    ) : this(
        navigationKey = navigationKey,
        id = UUID.randomUUID().toString()
    )

    override fun equals(other: Any?): Boolean {
        return other is Destination && other.id == id
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
}

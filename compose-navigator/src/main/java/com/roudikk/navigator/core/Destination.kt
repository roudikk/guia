package com.roudikk.navigator.core

import android.os.Parcelable
import com.roudikk.navigator.animation.NavTransition
import kotlinx.parcelize.Parcelize
import java.util.*

/**
 * Represents an entry in the navigation history.
 *
 * @property navigationNode, navigation node for this entry.
 * @property transition, transition animation.
 * @property id, unique identifier of the destination.
 */
@Parcelize
data class Destination(
    val navigationNode: NavigationNode,
    val transition: NavTransition,
    val id: String = UUID.randomUUID().toString()
) : Parcelable {

    override fun equals(other: Any?): Boolean {
        return other is Destination && other.id == id
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
}

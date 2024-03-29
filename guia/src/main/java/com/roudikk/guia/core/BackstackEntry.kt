package com.roudikk.guia.core

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.UUID

/**
 * Represents a unique entry in the backstack.
 *
 * @property id, unique identifier of the entry.
 * @property navigationKey, navigation key for this entry.
 */
@Parcelize
data class BackstackEntry internal constructor(
    val navigationKey: NavigationKey,
    val id: String
) : Parcelable {

    constructor(
        navigationKey: NavigationKey
    ) : this(
        navigationKey = navigationKey,
        id = UUID.randomUUID().toString()
    )
}

fun NavigationKey.entry() = BackstackEntry(this)
fun List<NavigationKey>.entries() = this.map(NavigationKey::entry)

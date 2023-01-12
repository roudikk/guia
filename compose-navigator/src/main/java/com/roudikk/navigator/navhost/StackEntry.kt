package com.roudikk.navigator.navhost

import com.roudikk.navigator.core.Navigator

/**
 * [StackEntry] is a relationship between a [Navigator] and a [StackEntry] inside a [NavHost].
 *
 * @property stackKey, the unique [StackKey] for a [Navigator].
 * @property navigator, the navigator associated with [stackKey].
 */
data class StackEntry(
    val stackKey: StackKey,
    val navigator: Navigator
) {

    override fun equals(other: Any?): Boolean {
        if (other !is StackEntry) return false
        return this.stackKey == other.stackKey
    }

    override fun hashCode(): Int {
        return stackKey.hashCode()
    }
}

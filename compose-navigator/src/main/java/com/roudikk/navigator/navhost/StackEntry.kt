package com.roudikk.navigator.navhost

import com.roudikk.navigator.core.Navigator
import com.roudikk.navigator.core.StackKey

class StackEntry(
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

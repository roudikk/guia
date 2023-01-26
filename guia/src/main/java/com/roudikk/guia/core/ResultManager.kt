package com.roudikk.guia.core

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.saveable.rememberSaveable
import com.roudikk.guia.savedstate.resultManagerSaver

/**
 * Manages results passed between navigation keys.
 */
interface ResultManager {
    fun result(key: String): Any?
    fun setResult(key: String, result: Any)
    fun clearResult(key: String)
}

/**
 * Returns a saveable instance of a [ResultManager]
 */
@Composable
fun rememberResultManager(): ResultManager {
    return rememberSaveable(saver = resultManagerSaver()) {
        NavigatorResultManager()
    }
}

/**
 * [ResultManager] implementation that uses a [mutableStateMapOf] to manage the results
 * in a stateful manner.
 */
class NavigatorResultManager : ResultManager {

    internal var results = mutableStateMapOf<String, Any?>()

    override fun result(key: String) = results[key]

    override fun setResult(key: String, result: Any) {
        results[key] = result
    }

    override fun clearResult(key: String) {
        results[key] = null
    }
}

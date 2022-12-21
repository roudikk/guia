package com.roudikk.navigator.core

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.saveable.rememberSaveable
import com.roudikk.navigator.savedstate.resultManagerSaver

interface ResultManager {
    fun result(key: String): Any?
    fun setResult(key: String, result: Any)
    fun clearResult(key: String)
}

@Composable
fun rememberResultManager(): ResultManager {
    return rememberSaveable(saver = resultManagerSaver()) {
        NavigatorResultManager()
    }
}

class NavigatorResultManager : ResultManager {

    internal var results = mutableStateMapOf<String, Any?>()

    override fun result(key: String): Any? {
        return results[key]
    }

    override fun setResult(key: String, result: Any) {
        results[key] = result
    }

    override fun clearResult(key: String) {
        results[key] = null
    }
}

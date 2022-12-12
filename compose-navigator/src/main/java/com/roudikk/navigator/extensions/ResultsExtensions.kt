package com.roudikk.navigator.extensions

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisallowComposableCalls
import androidx.compose.runtime.LaunchedEffect
import com.roudikk.navigator.core.Navigator

inline fun <reified Result : Any> Navigator.results(): Result? {
    return results(Result::class.java.simpleName) as Result?
}

inline fun <reified Result : Any> Navigator.pushResult(result: Result) {
    pushResult(Result::class.java.simpleName, result)
}

inline fun <reified Result : Any> Navigator.clearResult() {
    clearResult(Result::class.java.simpleName)
}

@Composable
inline fun <reified Result : Any> Navigator.onResult(
    crossinline onResult: @DisallowComposableCalls (Result) -> Unit
) {
    val result = results<Result>()
    LaunchedEffect(result) {
        result?.let(onResult)
        clearResult<Result>()
    }
}

@Composable
fun Navigator.onResult(
    key: String,
    onResult: @DisallowComposableCalls (Any) -> Unit
) {
    val result = results(key)
    LaunchedEffect(result) {
        result?.let(onResult)
        clearResult(key)
    }
}

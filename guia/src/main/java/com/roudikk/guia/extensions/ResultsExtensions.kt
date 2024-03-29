package com.roudikk.guia.extensions

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisallowComposableCalls
import androidx.compose.runtime.LaunchedEffect
import com.roudikk.guia.core.ResultManager

/**
 * Returns result of type [Result] using its class name as key.
 */
inline fun <reified Result : Any> ResultManager.result(): Result? {
    return result(Result::class.java.simpleName) as Result?
}

/**
 * Sets a result of type [Result] using its class name as key.
 *
 * @param result, the new updated result.
 */
inline fun <reified Result : Any> ResultManager.setResult(result: Result) {
    setResult(Result::class.java.simpleName, result)
}

/**
 * Clears a result of type [Result] using its class name as key.
 */
inline fun <reified Result : Any> ResultManager.clearResult() {
    clearResult(Result::class.java.simpleName)
}

/**
 * Returns the next result of type [Result] and clears it immediately, useful for handling short
 * lived results.
 *
 * @param onResult, lambda for consuming the result.
 */
@Composable
inline fun <reified Result : Any> ResultManager.onResult(
    crossinline onResult: @DisallowComposableCalls (Result) -> Unit
) {
    val result = result<Result>()
    LaunchedEffect(result) {
        result?.let(onResult)
        clearResult<Result>()
    }
}

/**
 * Returns the next result and clears it immediately, useful for handling short
 * lived results.
 *
 * @param key, the result key.
 * @param onResult, lambda for consuming the result.
 */
@SuppressLint("ComposableNaming")
@Composable
fun ResultManager.onResult(
    key: String,
    onResult: @DisallowComposableCalls (Any) -> Unit
) {
    val result = result(key)
    LaunchedEffect(result) {
        result?.let(onResult)
        clearResult(key)
    }
}

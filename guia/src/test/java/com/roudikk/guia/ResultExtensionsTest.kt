package com.roudikk.guia

import com.google.common.truth.Truth.assertThat
import com.roudikk.guia.core.NavigatorResultManager
import com.roudikk.guia.extensions.clearResult
import com.roudikk.guia.extensions.result
import com.roudikk.guia.extensions.setResult
import org.junit.Test

class ResultExtensionsTest {

    data class TestResult(val data: String)

    @Test
    fun setResult_updatesResult() {
        val resultManager = NavigatorResultManager()
        val result = TestResult("Data")
        val newResult = TestResult("Data 2")
        assertThat(resultManager.result(TestResult::class.java.simpleName))
            .isEqualTo(null)
        resultManager.setResult(result)
        assertThat(resultManager.result(TestResult::class.java.simpleName))
            .isEqualTo(result)
        resultManager.setResult(newResult)
        assertThat(resultManager.result(TestResult::class.java.simpleName))
            .isEqualTo(newResult)
    }

    @Test
    fun result_returnsResult() {
        val resultManager = NavigatorResultManager()
        val result = TestResult("Data")
        val newResult = TestResult("Data 2")
        assertThat(resultManager.result(TestResult::class.java.simpleName))
            .isEqualTo(null)
        assertThat(resultManager.result<TestResult>())
            .isEqualTo(null)
        resultManager.setResult(result)
        assertThat(resultManager.result(TestResult::class.java.simpleName))
            .isEqualTo(result)
        assertThat(resultManager.result<TestResult>())
            .isEqualTo(result)
        resultManager.setResult(newResult)
        assertThat(resultManager.result(TestResult::class.java.simpleName))
            .isEqualTo(newResult)
        assertThat(resultManager.result<TestResult>())
            .isEqualTo(newResult)
    }

    @Test
    fun clearResult_clearsResult() {
        val resultManager = NavigatorResultManager()
        val result = TestResult("Data")
        resultManager.setResult(result)
        assertThat(resultManager.result(TestResult::class.java.simpleName))
            .isEqualTo(result)
        assertThat(resultManager.result<TestResult>())
            .isEqualTo(result)
        resultManager.clearResult<TestResult>()
        assertThat(resultManager.result(TestResult::class.java.simpleName))
            .isEqualTo(null)
        assertThat(resultManager.result<TestResult>())
            .isEqualTo(null)
    }
}

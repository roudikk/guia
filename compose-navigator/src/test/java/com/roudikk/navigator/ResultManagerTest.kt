package com.roudikk.navigator

import com.google.common.truth.Truth.assertThat
import com.roudikk.navigator.core.NavigatorResultManager
import org.junit.Test

class ResultManagerTest {

    @Test
    fun resultManager_setResult_updatesResult() {
        val resultManager = NavigatorResultManager()
        assertThat(resultManager.result("Test")).isEqualTo(null)
        resultManager.setResult("Test", "Value")
        assertThat(resultManager.result("Test")).isEqualTo("Value")
        resultManager.setResult("Test", "Value 2")
        assertThat(resultManager.result("Test")).isEqualTo("Value 2")
    }

    @Test
    fun resultManager_clearResult_clearsResult() {
        val resultManager = NavigatorResultManager()
        assertThat(resultManager.result("Test")).isEqualTo(null)
        resultManager.setResult("Test", "Value")
        assertThat(resultManager.result("Test")).isEqualTo("Value")
        resultManager.clearResult("Test")
        assertThat(resultManager.result("Test")).isEqualTo(null)
    }
}

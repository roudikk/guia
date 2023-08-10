package com.roudikk.guia

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import com.roudikk.guia.containers.ScreenContainer
import com.roudikk.guia.core.entry
import com.roudikk.guia.core.rememberNavigator
import com.roudikk.guia.util.TestKey
import com.roudikk.guia.util.rememberLifecycleEntry
import org.junit.Rule
import org.junit.Test

class ScreenContainerTest {
    @get:Rule
    val rule = createComposeRule()

    @Test
    fun screenContainer_noEntry_notRendered() {
        rule.setContent {
            val navigator = rememberNavigator()
            navigator.ScreenContainer(screenEntry = null) {
                Box(modifier = Modifier.testTag("content"))
            }
        }

        rule.waitForIdle()
        rule.onNodeWithTag("screen_container").assertDoesNotExist()
        rule.onNodeWithTag("content").assertDoesNotExist()
    }

    @Test
    fun screenContainer_screenEntry_rendered() {
        rule.setContent {
            val backstackEntry = remember { TestKey().entry() }
            val entry = rememberLifecycleEntry(backstackEntry = backstackEntry)
            val navigator = rememberNavigator {
                screen<TestKey> { }
            }
            navigator.ScreenContainer(screenEntry = entry) {
                Box(modifier = Modifier.testTag("content"))
            }
        }

        rule.waitForIdle()
        rule.onNodeWithTag("screen_container").assertExists()
        rule.onNodeWithTag("content").assertExists()
    }
}

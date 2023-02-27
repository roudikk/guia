@file:OptIn(ExperimentalCoroutinesApi::class, ExperimentalCoroutinesApi::class)

package com.roudikk.guia

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import com.roudikk.guia.containers.DialogContainer
import com.roudikk.guia.core.entry
import com.roudikk.guia.core.rememberNavigator
import com.roudikk.guia.util.TestKey
import com.roudikk.guia.util.rememberLifecycleEntry
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import org.junit.Rule
import org.junit.Test

class DialogContainerTest {

    @get:Rule
    val rule = createComposeRule()

    @Test
    fun dialogContainer_noDialogEntry_notRendered() {
        rule.setContent {
            val navigator = rememberNavigator()

            navigator.DialogContainer(
                container = {},
                dialogEntry = null,
                content = {
                    Box(modifier = Modifier.testTag("content"))
                }
            )
        }

        TestScope().launch {
            rule.awaitIdle()
            rule.onNodeWithTag("dialog_container").assertDoesNotExist()
            rule.onNodeWithTag("content").assertDoesNotExist()
        }
    }

    @Test
    fun dialogContainer_dialogEntry_rendered() {
        rule.setContent {
            val backStackEntry = remember { TestKey().entry() }
            val navigator = rememberNavigator {
                dialog<TestKey> { }
            }
            val entry = rememberLifecycleEntry(backstackEntry = backStackEntry)

            navigator.DialogContainer(
                container = {},
                dialogEntry = entry,
                content = {
                    Box(modifier = Modifier.testTag("content"))
                }
            )
        }

        TestScope().launch {
            rule.awaitIdle()
            rule.onNodeWithTag("dialog_container").assertExists()
            rule.onNodeWithTag("content").assertExists()
        }
    }

    @Test
    fun dialogContainer_customContainer_rendered() {
        rule.setContent {
            val backStackEntry = remember { TestKey().entry() }
            val navigator = rememberNavigator {
                dialog<TestKey> { }
            }
            val entry = rememberLifecycleEntry(backstackEntry = backStackEntry)

            navigator.DialogContainer(
                container = { content ->
                    Box(
                        modifier = Modifier.testTag("custom_container"),
                    ) { content() }
                },
                dialogEntry = entry,
                content = {
                    Box(modifier = Modifier.testTag("content"))
                }
            )
        }

        TestScope().launch {
            rule.awaitIdle()
            rule.onNodeWithTag("dialog_container").assertExists()
            rule.onNodeWithTag("custom_container").assertExists()
            rule.onNodeWithTag("content").assertExists()
        }
    }
}

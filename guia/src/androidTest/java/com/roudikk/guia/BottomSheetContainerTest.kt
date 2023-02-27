package com.roudikk.guia

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import com.roudikk.guia.containers.BottomSheetContainer
import com.roudikk.guia.core.entry
import com.roudikk.guia.core.rememberNavigator
import com.roudikk.guia.util.TestKey
import com.roudikk.guia.util.rememberLifecycleEntry
import org.junit.Rule
import org.junit.Test

class BottomSheetContainerTest {

    @get:Rule
    val rule = createComposeRule()

    @Test
    fun bottomSheetContainer_noBottomSheetEntry_notRendered() {
        rule.setContent {
            val navigator = rememberNavigator()

            navigator.BottomSheetContainer(
                container = {},
                bottomSheetEntry = null,
                bottomSheetScrimColor = Color.Black,
                content = {
                    Box(modifier = Modifier.testTag("content"))
                }
            )
        }

        rule.waitForIdle()
        rule.onNodeWithTag("bottom_sheet_container").assertDoesNotExist()
        rule.onNodeWithTag("content").assertDoesNotExist()
    }

    @Test
    fun bottomSheetContainer_EntryExists_rendered() {
        rule.setContent {
            val backStackEntry = remember { TestKey().entry() }
            val navigator = rememberNavigator {
                bottomSheet<TestKey> { }
            }
            val entry = rememberLifecycleEntry(backstackEntry = backStackEntry)

            navigator.BottomSheetContainer(
                container = { content ->
                    Box(modifier = Modifier.testTag("custom_container")) {
                        content()
                    }
                },
                bottomSheetEntry = entry,
                bottomSheetScrimColor = Color.Black,
                content = {
                    Box(modifier = Modifier.testTag("content"))
                }
            )
        }

        rule.waitForIdle()
        rule.onNodeWithTag("bottom_sheet_container").assertExists()
        rule.onNodeWithTag("content").assertExists()
    }

    @Test
    fun bottomSheetContainer_customContainer_rendered() {
        rule.setContent {
            val backStackEntry = remember { TestKey().entry() }
            val navigator = rememberNavigator {
                bottomSheet<TestKey> { }
            }
            val entry = rememberLifecycleEntry(backstackEntry = backStackEntry)

            navigator.BottomSheetContainer(
                container = { content ->
                    Box(modifier = Modifier.testTag("custom_container")) {
                        content()
                    }
                },
                bottomSheetEntry = entry,
                bottomSheetScrimColor = Color.Black,
                content = {
                    Box(modifier = Modifier.testTag("content"))
                }
            )
        }

        rule.waitForIdle()
        rule.onNodeWithTag("bottom_sheet_container").assertExists()
        rule.onNodeWithTag("custom_container").assertExists()
        rule.onNodeWithTag("content").assertExists()
    }
}

package com.roudikk.guia

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.LocalSavedStateRegistryOwner
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import com.google.common.truth.Truth.assertThat
import com.roudikk.guia.backstack.DefaultRenderGroup
import com.roudikk.guia.containers.NavEntryContainer
import com.roudikk.guia.core.NavigationNode
import com.roudikk.guia.core.Navigator
import com.roudikk.guia.core.entry
import com.roudikk.guia.core.navigationNode
import com.roudikk.guia.core.rememberNavigator
import com.roudikk.guia.extensions.LocalNavigationNode
import com.roudikk.guia.extensions.LocalNavigator
import com.roudikk.guia.lifecycle.LifecycleEntry
import com.roudikk.guia.lifecycle.LifecycleManager
import com.roudikk.guia.lifecycle.rememberDefaultLifecycleManager
import com.roudikk.guia.util.TestKey
import com.roudikk.guia.util.rememberLifecycleEntry
import org.junit.Rule
import org.junit.Test

class NavEntryContainerTest {

    @get:Rule
    val rule = createComposeRule()

    @Test
    fun navEntryContainer_providesLocals() {
        val key = TestKey()
        val backstackEntry = key.entry()
        lateinit var lifecycleEntry: LifecycleEntry
        lateinit var navigator: Navigator
        lateinit var navigationNode: NavigationNode

        rule.setContent {
            navigator = rememberNavigator {
                screen<TestKey> {
                    assertThat(LocalLifecycleOwner.current).isEqualTo(lifecycleEntry)
                    assertThat(LocalViewModelStoreOwner.current).isEqualTo(lifecycleEntry)
                    assertThat(LocalNavigationNode.current).isEqualTo(navigationNode)
                    assertThat(LocalSavedStateRegistryOwner.current).isEqualTo(lifecycleEntry)
                    assertThat(LocalNavigator.current).isEqualTo(navigator)
                }
            }
            navigationNode = navigator.navigationNode(backstackEntry)
            lifecycleEntry = rememberLifecycleEntry(backstackEntry = backstackEntry)
            val lifecycleManager = rememberDefaultLifecycleManager(navigator = navigator)

            navigator.NavEntryContainer(
                lifecycleManager = lifecycleManager,
                lifecycleEntry = lifecycleEntry
            )
        }

        rule.waitForIdle()
        rule.onNodeWithTag(key.tag()).assertExists()
    }

    @Test
    fun navEntryContainer_onDispose_lifecycleManagerDispose() {
        lateinit var lifecycleManager: LifecycleManager<DefaultRenderGroup>

        rule.setContent {
            val navigator = rememberNavigator(initialKey = TestKey()) {
                screen<TestKey> {}
            }
            var shouldRender by remember { mutableStateOf(true) }
            var step by remember { mutableStateOf(1) }
            lifecycleManager = rememberDefaultLifecycleManager(navigator = navigator)

            if (shouldRender) {
                navigator.NavEntryContainer(
                    lifecycleManager = lifecycleManager,
                    lifecycleEntry = lifecycleManager.lifeCycleEntries.first()
                )
            }

            LaunchedEffect(step, shouldRender) {
                when (step) {
                    1 -> {
                        assertThat(lifecycleManager.lifeCycleEntries.first().lifecycle.currentState)
                            .isAtLeast(Lifecycle.State.INITIALIZED)
                        navigator.setBackstack()
                        shouldRender = false
                        step = 2
                    }

                    2 -> assertThat(lifecycleManager.lifeCycleEntries).isEmpty()
                }
            }
        }
    }
}

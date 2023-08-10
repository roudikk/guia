package com.roudikk.guia

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.lifecycle.Lifecycle
import com.google.common.truth.Truth.assertThat
import com.roudikk.guia.backstack.DefaultRenderGroup
import com.roudikk.guia.containers.ScreenContainer
import com.roudikk.guia.core.Navigator
import com.roudikk.guia.core.entries
import com.roudikk.guia.core.rememberNavigator
import com.roudikk.guia.lifecycle.LifecycleManager
import com.roudikk.guia.lifecycle.rememberDefaultLifecycleManager
import com.roudikk.guia.util.TestKey
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.junit.Rule
import org.junit.Test

class DefaultLifecycleManagerTest {

    @get:Rule
    val rule = createComposeRule()

    @Test
    fun emptyBackstack_NoLifecycleEntries() {
        lateinit var lifecycleManager: LifecycleManager<DefaultRenderGroup>
        lateinit var navigator: Navigator

        rule.setContent {
            navigator = rememberNavigator()
            lifecycleManager = rememberDefaultLifecycleManager(navigator = navigator)
        }

        rule.waitForIdle()
        assertThat(lifecycleManager.lifeCycleEntries).isEmpty()
    }

    @Test
    fun emptyBackstack_manyScreens_onlyLastResumed() {
        lateinit var lifecycleManager: LifecycleManager<DefaultRenderGroup>
        lateinit var navigator: Navigator

        rule.setContent {
            navigator = rememberNavigator {
                screen<TestKey> {}
            }
            lifecycleManager = rememberDefaultLifecycleManager(navigator = navigator)

            navigator.ScreenContainer(
                screenEntry = lifecycleManager.renderGroup.screenEntry
            ) {}
        }

        rule.waitForIdle()
        assertThat(lifecycleManager.lifeCycleEntries).isEmpty()

        val testEntries = listOf(TestKey(), TestKey(), TestKey()).entries()
        navigator.setBackstack(testEntries[0])
        rule.waitForIdle()
        assertThat(lifecycleManager.lifeCycleEntries).isNotEmpty()
        assertThat(lifecycleManager.lifeCycleEntries.first().backstackEntry)
            .isEqualTo(testEntries[0])
        assertThat(lifecycleManager.lifeCycleEntries.first().lifecycle.currentState)
            .isAtLeast(Lifecycle.State.RESUMED)

        navigator.setBackstack(testEntries[0], testEntries[1])
        rule.waitForIdle()
        assertThat(lifecycleManager.lifeCycleEntries).isNotEmpty()

        for (i in 0..1) {
            val entry = lifecycleManager.lifeCycleEntries[i]
            assertThat(entry.backstackEntry).isEqualTo(testEntries[i])
            if (entry.backstackEntry == testEntries[1]) {
                assertThat(entry.lifecycle.currentState).isEqualTo(Lifecycle.State.RESUMED)
            } else {
                assertThat(entry.lifecycle.currentState).isEqualTo(Lifecycle.State.CREATED)
            }
        }

        navigator.setBackstack(testEntries)
        rule.waitForIdle()
        assertThat(lifecycleManager.lifeCycleEntries).isNotEmpty()

        testEntries.forEachIndexed { index, _ ->
            val entry = lifecycleManager.lifeCycleEntries[index]
            assertThat(entry.backstackEntry).isEqualTo(testEntries[index])
            if (entry.backstackEntry == testEntries.last()) {
                assertThat(entry.lifecycle.currentState).isEqualTo(Lifecycle.State.RESUMED)
            } else {
                assertThat(entry.lifecycle.currentState).isEqualTo(Lifecycle.State.CREATED)
            }
        }

        navigator.setBackstack(testEntries[0])
        runBlocking(Dispatchers.Main) { lifecycleManager.onEntryDisposed() }
        rule.waitForIdle()
        assertThat(lifecycleManager.lifeCycleEntries.size).isEqualTo(1)
        assertThat(lifecycleManager.lifeCycleEntries[0].backstackEntry).isEqualTo(testEntries[0])
        assertThat(lifecycleManager.lifeCycleEntries[0].lifecycle.currentState)
            .isEqualTo(Lifecycle.State.RESUMED)

        navigator.setBackstack()
        runBlocking(Dispatchers.Main) {
            lifecycleManager.onEntryDisposed()
            lifecycleManager.onDispose()
        }
        rule.waitForIdle()
        assertThat(lifecycleManager.lifeCycleEntries).isEmpty()
    }
}

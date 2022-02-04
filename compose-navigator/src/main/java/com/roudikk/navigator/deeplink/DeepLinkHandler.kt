package com.roudikk.navigator.deeplink

import android.content.Intent
import com.roudikk.navigator.Navigator
import kotlinx.coroutines.*

abstract class DeepLinkHandler {
    internal val initialization = CompletableDeferred<Unit>()
    private val scope = CoroutineScope(Dispatchers.Main.immediate)

    internal var navigator: (String) -> Navigator = {
        error("Call NavHost with this deep link handler first")
    }

    abstract fun handleIntent(navigator: (String) -> Navigator, intent: Intent?)

    fun onIntent(intent: Intent?) {
        scope.launch {
            initialization.await()
            handleIntent(navigator, intent)
        }
    }
}

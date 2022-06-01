package com.roudikk.navigator.sample

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class DeepLinkViewModel : ViewModel() {
    private var handledInitialIntent = false

    private val mainDestinationsChannel = Channel<List<MainDestination>>()
    val mainDestinationsFlow = mainDestinationsChannel.receiveAsFlow()

    private val tabDestinationsChannel = Channel<List<TabDestination>>()
    val tabDestinationsFlow = tabDestinationsChannel.receiveAsFlow()

    private val nestedDestinationsChannel = Channel<List<NestedDestination>>()
    val nestedDestinationsFlow = nestedDestinationsChannel.receiveAsFlow()

    var mainDestinations = emptyList<MainDestination>()
        private set
        get() = field.also { field = emptyList() }

    var tabDestinations = emptyList<TabDestination>()
        private set
        get() = field.also { field = emptyList() }

    var nestedDestinations = emptyList<NestedDestination>()
        private set
        get() = field.also { field = emptyList() }

    fun onCreate(intentData: String?) {
        if (handledInitialIntent) return
        handledInitialIntent = true
        intentData ?: return
        parseIntent(intentData, false)
    }

    fun onNewIntent(intentData: String?) {
        intentData ?: return
        parseIntent(intentData, true)
    }

    private fun parseIntent(intentData: String, newIntent: Boolean) {
        val uri = runCatching { Uri.parse(intentData) }.getOrNull() ?: return

        if (uri.host?.contains("roudikk") == false || uri.scheme != "https") return

        val mainDestinations = mutableListOf<MainDestination>()
        val tabDestinations = mutableListOf<TabDestination>()
        val nestedDestinations = mutableListOf<NestedDestination>()

        if (uri.pathSegments.contains("bottom-nav")) {
            mainDestinations.add(MainDestination.BottomNav)

            if (uri.pathSegments.contains("home")) {
                tabDestinations.add(TabDestination.HomeTab)

                if (uri.pathSegments.contains("details")) {
                    uri.getQueryParameter("item")?.let {
                        tabDestinations.add(TabDestination.Details(it))
                    }
                }
            }

            if (uri.pathSegments.contains("nested")) {
                tabDestinations.add(TabDestination.NestedTab)

                uri.getQueryParameter("count")?.let {
                    val count = kotlin.runCatching { it.toInt() }.getOrNull() ?: return@let
                    nestedDestinations.add(NestedDestination.Nested(count))
                }
            }

            if (uri.pathSegments.contains("dialogs")) {
                tabDestinations.add(TabDestination.DialogsTab)
            }

            if (uri.pathSegments.contains("stack-tree")) {
                tabDestinations.add(TabDestination.StackTreeTab)
            }
        }

        if (uri.pathSegments.contains("settings")) {
            mainDestinations.add(MainDestination.Settings)
        }

        if (newIntent) {
            viewModelScope.launch {
                mainDestinationsChannel.send(mainDestinations)
                tabDestinationsChannel.send(tabDestinations)
                nestedDestinationsChannel.send(nestedDestinations)
            }
        } else {
            this.mainDestinations = mainDestinations
            this.tabDestinations = tabDestinations
            this.nestedDestinations = nestedDestinations
        }
    }
}

sealed class MainDestination {
    object BottomNav : MainDestination()
    object Settings : MainDestination()
}

sealed class TabDestination {
    object HomeTab : TabDestination()
    object NestedTab : TabDestination()
    object DialogsTab : TabDestination()
    object StackTreeTab : TabDestination()
    data class Details(val item: String) : TabDestination()
}

sealed class NestedDestination {
    data class Nested(val count: Int) : NestedDestination()
}

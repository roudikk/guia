package com.roudikk.navigator.sample.feature.common.deeplink

import android.net.Uri
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class GlobalNavigator : ViewModel() {

    var destinations by mutableStateOf(emptyList<NavigationDestination>())
        private set

    val mainDestinations by derivedStateOf {
        destinations.filterIsInstance<MainDestination>()
    }

    val homeDestinations by derivedStateOf {
        destinations.filterIsInstance<HomeDestination>()
    }

    val nestedDestinations by derivedStateOf {
        destinations.filterIsInstance<NestedDestination>()
    }

    val bottomTabDestinations by derivedStateOf {
        destinations.filterIsInstance<BottomTabDestination>()
    }

    val dialogsDestinations by derivedStateOf {
        destinations.filterIsInstance<DialogsDestination>()
    }

    fun onDeeplinkData(data: String?) {
        data ?: return
        val uri = runCatching { Uri.parse(data) }.getOrNull() ?: return
        destinations = buildList {
            uri.pathSegments.forEachIndexed { index, segment ->
                when (segment) {
                    "bottom-tab" -> addAll(bottomNavDestinations(uri, index))
                    "settings" -> add(MainDestination.Settings)
                }
            }
        }
    }

    private fun bottomNavDestinations(
        uri: Uri,
        index: Int
    ) = buildList {
        add(MainDestination.BottomNav)
        uri.pathSegments.subList(index, uri.pathSegments.size)
            .forEachIndexed { nestedIndex, segment ->
                when (segment) {
                    "home" -> addAll(homeDestinations(uri, nestedIndex))
                    "nested" -> addAll(nestedDestinations(uri, nestedIndex))
                    "dialogs" -> addAll(dialogDestinations(uri, nestedIndex))
                    "custom" -> add(BottomTabDestination.CustomTab)
                }
            }
    }

    private fun homeDestinations(
        uri: Uri,
        index: Int
    ) = buildList {
        add(BottomTabDestination.HomeTab)
        when (uri.pathSegments.getOrNull(index + 1)) {
            "details" -> uri.getQueryParameter("item")
                ?.let { item -> add(HomeDestination.Details(item)) }
        }
    }

    private fun nestedDestinations(
        uri: Uri,
        index: Int
    ) = buildList {
        add(BottomTabDestination.NestedTab)
        when (uri.pathSegments.getOrNull(index + 1)) {
            "slide" -> uri.getQueryParameter("item")
                ?.toIntOrNull()
                ?.let { item -> add(NestedDestination.Nested(item)) }
        }
    }

    private fun dialogDestinations(
        uri: Uri,
        index: Int
    ) = buildList {
        add(BottomTabDestination.DialogsTab)
        when (uri.pathSegments.getOrNull(index + 1)) {
            "cancelable" -> add(DialogsDestination.Cancelable)
            "blocking-dialog" -> add(DialogsDestination.BlockingDialog)
            "blocking-bottom-sheet" -> add(DialogsDestination.BlockingBottomSheet)
        }
    }

    fun navigateToDetails(item: String) {
        destinations = listOf(
            MainDestination.BottomNav,
            BottomTabDestination.HomeTab,
            HomeDestination.Details(item)
        )
    }

    fun onMainDestinationsHandled() {
        destinations = destinations.filter { it !is MainDestination }
    }

    fun onNestedDestinationsHandled() {
        destinations = destinations.filter { it !is NestedDestination }
    }

    fun onBottomTabDestinationsHandled() {
        destinations = destinations.filter { it !is BottomTabDestination }
    }

    fun onHomeDestinationsHandled() {
        destinations = destinations.filter { it !is HomeDestination }
    }

    fun onDialogsDestinationsHandled() {
        destinations = destinations.filter { it !is DialogsDestination }
    }
}

sealed class NavigationDestination

sealed class MainDestination : NavigationDestination() {
    object BottomNav : MainDestination()
    object Settings : MainDestination()
}

sealed class HomeDestination : NavigationDestination() {
    class Details(val item: String) : HomeDestination()
}

sealed class DialogsDestination : NavigationDestination() {
    object Cancelable : DialogsDestination()
    object BlockingDialog : DialogsDestination()
    object BlockingBottomSheet : DialogsDestination()
}

sealed class BottomTabDestination : NavigationDestination() {
    object HomeTab : BottomTabDestination()
    object NestedTab : BottomTabDestination()
    object DialogsTab : BottomTabDestination()
    object CustomTab : BottomTabDestination()
}

sealed class NestedDestination : NavigationDestination() {
    class Nested(val index: Int) : NestedDestination()
}

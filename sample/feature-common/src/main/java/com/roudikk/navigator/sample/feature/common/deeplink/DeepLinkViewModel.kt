package com.roudikk.navigator.sample.feature.common.deeplink

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class DeepLinkViewModel : ViewModel() {

    var destinations by mutableStateOf(emptyList<DeepLinkDestination>())
        private set

    fun onDeeplinkData(data: String?) {
        destinations = emptyList()

        data ?: return
        val uri = runCatching { Uri.parse(data) }.getOrNull() ?: return

        val newDestinations = mutableListOf<DeepLinkDestination>()
        uri.pathSegments.forEachIndexed { index, segment ->
            when (segment) {
                "bottom-nav" -> {
                    newDestinations.add(MainDestination.BottomNav)
                    uri.pathSegments.subList(index, uri.pathSegments.size)
                        .forEachIndexed { bottomNavIndex, bottomNavSegment ->
                            when (bottomNavSegment) {
                                "home" -> {
                                    newDestinations.add(BottomNavDestination.HomeTab)
                                    when (uri.pathSegments.getOrNull(bottomNavIndex + 1)) {
                                        "details" -> {
                                            uri.getQueryParameter("item")?.let { item ->
                                                newDestinations.add(HomeDestination.Details(item))
                                            }
                                        }
                                    }
                                }

                                "parent-nested" -> {
                                    newDestinations.add(BottomNavDestination.NestedTab)
                                    when (uri.pathSegments.getOrNull(bottomNavIndex + 1)) {
                                        "nested" -> {
                                            uri.getQueryParameter("item")
                                                ?.toIntOrNull()
                                                ?.let { item ->
                                                    newDestinations.add(
                                                        NestedDestination.Nested(
                                                            item
                                                        )
                                                    )
                                                }
                                        }
                                    }
                                }

                                "dialogs" -> {
                                    newDestinations.add(BottomNavDestination.DialogsTab)
                                    when (uri.pathSegments.getOrNull(bottomNavIndex + 1)) {
                                        "cancelable" -> newDestinations.add(
                                            DialogsDestination.Cancelable
                                        )

                                        "blocking-dialog" -> newDestinations.add(
                                            DialogsDestination.BlockingDialog
                                        )

                                        "blocking-bottom-sheet" -> newDestinations.add(
                                            DialogsDestination.BlockingBottomSheet
                                        )
                                    }
                                }

                                "navigation-tree" -> newDestinations.add(BottomNavDestination.NavigationTreeTab)
                            }
                        }
                }

                "settings" -> newDestinations.add(MainDestination.Settings)
            }
        }

        destinations = newDestinations
    }

    fun onMainDestinationsHandled() {
        destinations = destinations.filter { it !is MainDestination }
    }

    fun onNestedDestinationsHandled() {
        destinations = destinations.filter { it !is NestedDestination }
    }

    fun onBottomNavDestinationsHandled() {
        destinations = destinations.filter {
            it !is BottomNavDestination && it !is DialogsDestination && it !is HomeDestination
        }
    }
}

sealed class DeepLinkDestination

sealed class MainDestination : DeepLinkDestination() {
    object BottomNav : MainDestination()
    object Settings : MainDestination()
}

sealed class HomeDestination : DeepLinkDestination() {
    class Details(val item: String) : HomeDestination()
}

sealed class DialogsDestination : DeepLinkDestination() {
    object Cancelable : DialogsDestination()
    object BlockingDialog : DialogsDestination()
    object BlockingBottomSheet : DialogsDestination()
}

sealed class BottomNavDestination : DeepLinkDestination() {
    object HomeTab : BottomNavDestination()
    object NestedTab : BottomNavDestination()
    object DialogsTab : BottomNavDestination()
    object NavigationTreeTab : BottomNavDestination()
}

sealed class NestedDestination : DeepLinkDestination() {
    class Nested(val index: Int) : NestedDestination()
}

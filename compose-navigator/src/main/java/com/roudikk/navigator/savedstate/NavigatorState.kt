package com.roudikk.navigator.savedstate

import android.os.Parcelable
import com.roudikk.navigator.Navigator
import com.roudikk.navigator.core.NavigationConfig
import com.roudikk.navigator.core.NavigationState
import com.roudikk.navigator.core.NavHistoryEntry
import kotlinx.parcelize.Parcelize

/**
 * Used to hold the state of a [Navigator] for state saving/restoration.
 */
@Parcelize
internal data class NavigatorState(
    val navigationState: NavigationState,
    val navigationConfig: NavigationConfig,
    val stackHistory: List<NavHistoryEntry>
) : Parcelable

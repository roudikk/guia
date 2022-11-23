package com.roudikk.navigator.core

import android.os.Parcelable
import com.roudikk.navigator.animation.NavEnterExitTransition
import kotlinx.parcelize.Parcelize

@Parcelize
data class NavigationState(
    val destinations: List<Destination>,
    val transition: NavEnterExitTransition
) : Parcelable

package com.roudikk.navigator.extensions

import com.roudikk.navigator.animation.EnterExitTransition
import com.roudikk.navigator.core.NavigationKey
import com.roudikk.navigator.core.NavigationNode
import kotlin.reflect.KClass

internal typealias Presentations =
    HashMap<KClass<NavigationKey>, (NavigationKey) -> NavigationNode>

internal typealias Transitions =
    HashMap<KClass<NavigationKey>, (previous: NavigationKey, new: NavigationKey, isPop: Boolean) -> EnterExitTransition>

internal typealias Transition =
        (previous: NavigationKey, new: NavigationKey, isPop: Boolean) -> EnterExitTransition

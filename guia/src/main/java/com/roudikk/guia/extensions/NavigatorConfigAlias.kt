package com.roudikk.guia.extensions

import com.roudikk.guia.animation.EnterExitTransition
import com.roudikk.guia.core.NavigationKey
import com.roudikk.guia.core.NavigationNode
import kotlin.reflect.KClass

internal typealias Presentations =
    HashMap<KClass<NavigationKey>, (NavigationKey) -> NavigationNode>

internal typealias Transitions =
    HashMap<KClass<NavigationKey>, (previous: NavigationKey, new: NavigationKey, isPop: Boolean) -> EnterExitTransition>

internal typealias Transition =
    (previous: NavigationKey, new: NavigationKey, isPop: Boolean) -> EnterExitTransition

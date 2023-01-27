package com.roudikk.guia.extensions

import com.roudikk.guia.animation.EnterExitTransition
import com.roudikk.guia.core.NavigationKey
import com.roudikk.guia.core.NavigationNode
import kotlin.reflect.KClass

internal typealias Presentations = HashMap<KClass<out NavigationKey>, (NavigationKey) -> NavigationNode>
internal typealias KeyTransitions = HashMap<KClass<out NavigationKey>, Transition>
internal typealias NodeTransitions = HashMap<KClass<out NavigationNode>, Transition>
internal typealias Transition = (previous: NavigationKey, new: NavigationKey, isPop: Boolean) -> EnterExitTransition

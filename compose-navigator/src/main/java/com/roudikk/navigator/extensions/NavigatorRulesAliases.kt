package com.roudikk.navigator.extensions

import com.roudikk.navigator.animation.EnterExitTransition
import com.roudikk.navigator.core.NavigationKey
import com.roudikk.navigator.core.NavigationNode
import kotlin.reflect.KClass

internal typealias AssociationsMap = HashMap<KClass<NavigationKey>, (NavigationKey) -> NavigationNode>
internal typealias TransitionsMap = HashMap<KClass<NavigationKey>, (previous: NavigationKey, new: NavigationKey, isPop: Boolean) -> EnterExitTransition>
internal typealias NavigationNodeTransition = (previous: NavigationKey, new: NavigationKey, isPop: Boolean) -> EnterExitTransition

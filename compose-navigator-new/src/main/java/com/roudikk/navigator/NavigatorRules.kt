package com.roudikk.navigator

import com.roudikk.navigator.core.NavigationNode

class NavigatorRules(
    val associations: HashMap<NavigationKey, () -> NavigationNode> = hashMapOf()
)
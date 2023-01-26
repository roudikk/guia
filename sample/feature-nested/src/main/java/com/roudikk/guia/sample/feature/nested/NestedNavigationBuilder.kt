package com.roudikk.guia.sample.feature.nested

import com.roudikk.guia.core.NavigatorConfigBuilder
import com.roudikk.guia.sample.feature.nested.navigation.NestedKey
import com.roudikk.guia.sample.feature.nested.navigation.ParentNestedKey

fun NavigatorConfigBuilder.nestedNavigation() {
    screen<ParentNestedKey> { ParentNestedScreen() }
    screen<NestedKey> { NestedScreen(count = it.index) }
}

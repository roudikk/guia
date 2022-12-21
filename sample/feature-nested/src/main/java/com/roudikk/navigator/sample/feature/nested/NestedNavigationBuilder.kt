package com.roudikk.navigator.sample.feature.nested

import com.roudikk.navigator.core.NavigatorConfigBuilder
import com.roudikk.navigator.sample.feature.nested.api.NestedKey
import com.roudikk.navigator.sample.feature.nested.api.ParentNestedKey

fun NavigatorConfigBuilder.nestedNavigation() {
    screen<ParentNestedKey> { ParentNestedScreen() }
    screen<NestedKey> { NestedScreen(count = it.index) }
}

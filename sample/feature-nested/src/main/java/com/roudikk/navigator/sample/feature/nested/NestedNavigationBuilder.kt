package com.roudikk.navigator.sample.feature.nested

import com.roudikk.navigator.NavigatorBuilderScope
import com.roudikk.navigator.sample.feature.nested.api.NestedKey
import com.roudikk.navigator.sample.feature.nested.api.ParentNestedKey

fun NavigatorBuilderScope.nestedNavigation() {
    screen<ParentNestedKey> { ParentNestedScreen() }
    screen<NestedKey> { NestedScreen(count = it.index) }
}

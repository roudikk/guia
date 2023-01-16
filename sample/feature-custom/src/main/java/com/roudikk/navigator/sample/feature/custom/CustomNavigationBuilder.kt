package com.roudikk.navigator.sample.feature.custom

import com.roudikk.navigator.core.NavigatorConfigBuilder
import com.roudikk.navigator.sample.feature.custom.api.CustomKey
import com.roudikk.navigator.sample.feature.custom.api.CustomRootKey

fun NavigatorConfigBuilder.customNavigation() {
    screen<CustomRootKey> { CustomRootScreen() }
    custom<CustomKey> { CustomScreen(it.id) }
}

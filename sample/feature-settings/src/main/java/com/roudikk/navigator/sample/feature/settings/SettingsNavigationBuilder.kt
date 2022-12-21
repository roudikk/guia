package com.roudikk.navigator.sample.feature.settings

import com.roudikk.navigator.core.NavigatorConfigBuilder
import com.roudikk.navigator.sample.feature.settings.api.SettingsKey

fun NavigatorConfigBuilder.settingsNavigation() {
    screen<SettingsKey> { SettingsScreen()  }
}

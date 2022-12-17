package com.roudikk.navigator.sample.feature.settings

import com.roudikk.navigator.NavigatorBuilderScope
import com.roudikk.navigator.sample.feature.settings.api.SettingsKey

fun NavigatorBuilderScope.settingsNavigation() {
    screen<SettingsKey> { SettingsScreen()  }
}

package com.roudikk.navigator.sample.feature.settings

import com.roudikk.navigator.core.NavigatorConfigScope
import com.roudikk.navigator.sample.feature.settings.api.SettingsKey

fun NavigatorConfigScope.settingsNavigation() {
    screen<SettingsKey> { SettingsScreen()  }
}

package com.roudikk.guia.sample.feature.settings

import com.roudikk.guia.core.NavigatorConfigBuilder
import com.roudikk.guia.sample.feature.settings.api.SettingsKey

fun NavigatorConfigBuilder.settingsNavigation() {
    screen<SettingsKey> { SettingsScreen()  }
}

@file:Suppress("UnstableApiUsage")

plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("io.gitlab.arturbosch.detekt")
    id("kotlin-parcelize")
}

android {
    namespace = "com.roudikk.guia.sample.feature.common"
    compileSdk = 34

    defaultConfig {
        minSdk = 24
    }

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.composeCompiler.get()
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    // Material
    api(libs.google.material)

    // Coroutines
    api(libs.coroutines)

    // Compose
    api(platform(libs.compose.bom))
    api(libs.compose.material)
    api(libs.compose.ui)
    api(libs.compose.viewModel)
    api(libs.compose.ui.tooling)
    api(libs.compose.foundation)
    api(libs.compose.material.icons)
    api(libs.compose.activity)
    api(libs.compose.material3)

    // Accompanist
    api(libs.accompanist.pager)
    api(libs.accompanist.systemuicontroller)
    api(libs.accompanist.pager.indicators)

    // Detekt
    detektPlugins(libs.detekt.formatting)

    // Lottie
    api(libs.lottie)

    // Navigator
    api(project(":guia"))
}

detekt {
    autoCorrect = true
    buildUponDefaultConfig = true // preconfigure defaults
    allRules = false // activate all available (even unstable) rules.
}

tasks.withType<io.gitlab.arturbosch.detekt.Detekt>().configureEach {
    autoCorrect = true
    reports {
        html.required.set(true) // observe findings in your browser with structure and code snippets
        xml.required.set(true) // checkstyle like format mainly for integrations like Jenkins
        // Similar to the console output, contains issue signature to
        // manually edit baseline files
        txt.required.set(true)
        // Standardized SARIF format (https://sarifweb.azurewebsites.net/)
        // to support integrations with Github Code Scanning
        sarif.required.set(true)
    }
}

tasks.withType<io.gitlab.arturbosch.detekt.Detekt>().configureEach {
    jvmTarget = "17"
}

tasks.withType<io.gitlab.arturbosch.detekt.DetektCreateBaselineTask>().configureEach {
    jvmTarget = "17"
}

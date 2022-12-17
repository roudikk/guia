plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("io.gitlab.arturbosch.detekt")
    id("kotlin-parcelize")
}

val composeVersion = "1.3.1"
val composeCompilerVersion = "1.3.2"
val composeMaterial3Version = "1.0.1"
val accompanistVersion = "0.27.1"
val lottieVersion = "5.2.0"
val kotlinCoroutinesVersion = "1.6.4"
val activityComposeVersion = "1.6.1"
val viewModelVersion = "2.5.1"
val junitVersion = "4.13.2"
val jupiterVersion = "5.9.1"
val truthVersion = "1.1.3"
val materialVersion = "1.7.0"
val detektVersion = "1.21.0"

android {
    namespace = "com.roudikk.navigator.sample.feature.common"
    compileSdk = 33

    defaultConfig {
        minSdk = 24
        targetSdk = 33
    }

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = composeCompilerVersion
    }
}

dependencies {
    // Material
    api("com.google.android.material:material:$materialVersion")

    // Coroutines
    api("org.jetbrains.kotlinx:kotlinx-coroutines-core:$kotlinCoroutinesVersion")

    // Compose
    api("androidx.compose.material:material:$composeVersion")
    api("androidx.compose.ui:ui:$composeVersion")
    api("androidx.compose.ui:ui-tooling:$composeVersion")
    api("androidx.compose.foundation:foundation:$composeVersion")
    api("androidx.compose.material:material-icons-core:$composeVersion")
    api("androidx.compose.material:material-icons-extended:$composeVersion")
    api("androidx.activity:activity-compose:$activityComposeVersion")
    api("androidx.lifecycle:lifecycle-viewmodel-compose:$viewModelVersion")
    api("androidx.compose.material3:material3:$composeMaterial3Version")

    // Accompanist
    api("com.google.accompanist:accompanist-systemuicontroller:$accompanistVersion")
    api("com.google.accompanist:accompanist-insets:$accompanistVersion")
    api("com.google.accompanist:accompanist-pager:$accompanistVersion")
    api("com.google.accompanist:accompanist-pager-indicators:$accompanistVersion")

    // Detekt
    detektPlugins("io.gitlab.arturbosch.detekt:detekt-formatting:$detektVersion")

    // Lottie
    api("com.airbnb.android:lottie-compose:$lottieVersion")

    // Navigator
    api(project(":compose-navigator"))

    // Test dependencies
    androidTestApi("androidx.compose.ui:ui-test-junit4:$composeVersion")
    debugApi("androidx.compose.ui:ui-test-manifest:$composeVersion")
    testApi("junit:junit:$junitVersion")
    testApi("org.junit.jupiter:junit-jupiter:$jupiterVersion")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:$jupiterVersion")
    testApi("com.google.truth:truth:$truthVersion")
    androidTestApi("androidx.test.espresso:espresso-core:3.5.0")
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
    jvmTarget = "1.8"
}

tasks.withType<io.gitlab.arturbosch.detekt.DetektCreateBaselineTask>().configureEach {
    jvmTarget = "1.8"
}

@file:Suppress("UnstableApiUsage")

import io.gitlab.arturbosch.detekt.Detekt
import io.gitlab.arturbosch.detekt.DetektCreateBaselineTask

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

plugins {
    id("com.android.application")
    kotlin("android")
    id("kotlin-parcelize")
    id("io.gitlab.arturbosch.detekt")
}

// Module wide Opt ins for experimental compose apis

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    kotlinOptions.freeCompilerArgs += "-opt-in=kotlin.RequiresOptIn"
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    kotlinOptions.freeCompilerArgs += "-opt-in=androidx.compose.animation.ExperimentalAnimationApi"
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    kotlinOptions.freeCompilerArgs += "-opt-in=androidx.compose.material3.ExperimentalMaterial3Api"
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    kotlinOptions.freeCompilerArgs += "-opt-in=androidx.compose.material.ExperimentalMaterialApi"
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    kotlinOptions.freeCompilerArgs += "-opt-in=androidx.compose.foundation.ExperimentalFoundationApi"
}

android {
    compileSdk = 33

    defaultConfig {
        applicationId = "com.roudikk.navigator.sample"
        minSdk = 24
        targetSdk = 33
        versionCode = 3
        versionName = "1.2"
        signingConfig = signingConfigs.getByName("debug")
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        getByName("debug") {
            applicationIdSuffix = ".debug"
        }
    }

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = composeCompilerVersion
    }
    namespace = "com.roudikk.navigator.sample"
}

dependencies {
    // Material
    implementation("com.google.android.material:material:$materialVersion")

    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$kotlinCoroutinesVersion")

    // Compose
    implementation("androidx.compose.material:material:$composeVersion")
    implementation("androidx.compose.ui:ui:$composeVersion")
    implementation("androidx.compose.ui:ui-tooling:$composeVersion")
    implementation("androidx.compose.foundation:foundation:$composeVersion")
    implementation("androidx.compose.material:material-icons-core:$composeVersion")
    implementation("androidx.compose.material:material-icons-extended:$composeVersion")
    implementation("androidx.activity:activity-compose:$activityComposeVersion")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:$viewModelVersion")
    implementation("androidx.compose.material3:material3:$composeMaterial3Version")

    // Accompanist
    implementation("com.google.accompanist:accompanist-systemuicontroller:$accompanistVersion")
    implementation("com.google.accompanist:accompanist-insets:$accompanistVersion")
    implementation("com.google.accompanist:accompanist-pager:$accompanistVersion")
    implementation("com.google.accompanist:accompanist-pager-indicators:$accompanistVersion")

    // Detekt
    detektPlugins("io.gitlab.arturbosch.detekt:detekt-formatting:$detektVersion")

    // Lottie
    implementation("com.airbnb.android:lottie-compose:$lottieVersion")
    implementation(project(":compose-navigator"))

    // Navigator

    // Test dependencies
    androidTestImplementation("androidx.compose.ui:ui-test-junit4:$composeVersion")
    debugImplementation("androidx.compose.ui:ui-test-manifest:$composeVersion")
    testImplementation("junit:junit:$junitVersion")
    testImplementation("org.junit.jupiter:junit-jupiter:$jupiterVersion")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:$jupiterVersion")
    testImplementation("com.google.truth:truth:$truthVersion")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.0")
}

detekt {
    autoCorrect = true
    buildUponDefaultConfig = true // preconfigure defaults
    allRules = false // activate all available (even unstable) rules.
}

tasks.withType<Detekt>().configureEach {
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

tasks.withType<Detekt>().configureEach {
    jvmTarget = "1.8"
}

tasks.withType<DetektCreateBaselineTask>().configureEach {
    jvmTarget = "1.8"
}

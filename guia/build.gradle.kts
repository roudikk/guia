@file:Suppress("UnstableApiUsage")

import io.gitlab.arturbosch.detekt.Detekt
import io.gitlab.arturbosch.detekt.DetektCreateBaselineTask

plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("kotlin-parcelize")
    id("com.vanniktech.maven.publish")
    id("io.gitlab.arturbosch.detekt")
}

android {
    compileSdk = 34

    defaultConfig {
        minSdk = 21

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.composeCompiler.get()
    }

    buildFeatures {
        compose = true
    }

    namespace = "com.roudikk.guia"
}

dependencies {
    // Compose
    implementation(platform(libs.compose.bom))
    implementation(libs.compose.viewModel)
    implementation(libs.compose.ui)
    implementation(libs.compose.material)
    implementation(libs.compose.animation)
    implementation(libs.compose.activity)
    implementation(libs.coroutines)

    // Detekt
    detektPlugins(libs.detekt.formatting)

    // Unit test
    testImplementation(libs.coroutines.test)
    testImplementation(libs.junit)
    testImplementation(libs.google.truth)
    debugImplementation(libs.compose.ui.test)

    // Android test
    androidTestImplementation(libs.junit.android)
    androidTestImplementation(libs.compose.ui.junit)
    androidTestImplementation(libs.espresso)
    androidTestImplementation(libs.google.truth)
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    kotlinOptions.freeCompilerArgs += "-opt-in=kotlin.RequiresOptIn"
}

detekt {
    autoCorrect = true
    buildUponDefaultConfig = true
    allRules = false
}

tasks.withType<Detekt>().configureEach {
    jvmTarget = "17"
}

tasks.withType<DetektCreateBaselineTask>().configureEach {
    jvmTarget = "117"
}

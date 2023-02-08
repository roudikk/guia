@file:Suppress("UnstableImplementationUsage", "UnstableApiUsage")

import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("com.android.application")
    kotlin("android")
    id("kotlin-parcelize")
}

android {
    compileSdk = 33

    defaultConfig {
        applicationId = "com.roudikk.guia.sample"
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
        kotlinCompilerExtensionVersion = libs.versions.composeCompiler.get()
    }

    namespace = "com.roudikk.guia.sample"
}

dependencies {
    implementation(project(":sample:feature-common"))
    implementation(project(":sample:feature-welcome"))
    implementation(project(":sample:feature-home"))
    implementation(project(":sample:feature-nested"))
    implementation(project(":sample:feature-dialogs"))
    implementation(project(":sample:feature-bottomnav"))
    implementation(project(":sample:feature-settings"))
    implementation(project(":sample:feature-details"))
    implementation(project(":sample:feature-common"))
    implementation(project(":sample:feature-welcome:navigation"))
    implementation(project(":sample:feature-home:navigation"))
    implementation(project(":sample:feature-nested:navigation"))
    implementation(project(":sample:feature-dialogs:navigation"))
    implementation(project(":sample:feature-bottomnav:navigation"))
    implementation(project(":sample:feature-settings:navigation"))
    implementation(project(":sample:feature-details:navigation"))
    implementation(project(":sample:feature-custom"))
    implementation(project(":sample:feature-custom:navigation"))
    implementation(project(":guia"))

    // Test
    testImplementation(libs.junit)
    debugImplementation(libs.compose.ui.test)
    androidTestImplementation(libs.androidx.test.core)
    androidTestImplementation(libs.compose.ui.junit)
    androidTestImplementation(libs.espresso)
}

tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions.freeCompilerArgs += "-opt-in=kotlin.RequiresOptIn"
}

tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions.freeCompilerArgs += "-opt-in=androidx.compose.animation.ExperimentalAnimationImplementation"
}

tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions.freeCompilerArgs += "-opt-in=androidx.compose.material3.ExperimentalMaterial3Implementation"
}

tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions.freeCompilerArgs += "-opt-in=androidx.compose.material.ExperimentalMaterialImplementation"
}

tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions.freeCompilerArgs += "-opt-in=androidx.compose.foundation.ExperimentalFoundationImplementation"
}

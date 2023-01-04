@file:Suppress("UnstableImplementationUsage")

plugins {
    id("com.android.application")
    kotlin("android")
    id("kotlin-parcelize")
}

val composeVersion = "1.3.2"
val jupiterVersion = "5.9.1"
val junitVersion = "4.13.2"
val truthVersion = "1.1.3"

// Module wide Opt ins for experimental compose apis

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    kotlinOptions.freeCompilerArgs += "-opt-in=kotlin.RequiresOptIn"
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    kotlinOptions.freeCompilerArgs += "-opt-in=androidx.compose.animation.ExperimentalAnimationImplementation"
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    kotlinOptions.freeCompilerArgs += "-opt-in=androidx.compose.material3.ExperimentalMaterial3Implementation"
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    kotlinOptions.freeCompilerArgs += "-opt-in=androidx.compose.material.ExperimentalMaterialImplementation"
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    kotlinOptions.freeCompilerArgs += "-opt-in=androidx.compose.foundation.ExperimentalFoundationImplementation"
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
        kotlinCompilerExtensionVersion = "1.3.2"
    }

    namespace = "com.roudikk.navigator.sample"
}

dependencies {
    implementation(project(":sample:feature-common"))
    implementation(project(":sample:feature-welcome"))
    implementation(project(":sample:feature-home"))
    implementation(project(":sample:feature-nested"))
    implementation(project(":sample:feature-dialogs"))
    implementation(project(":sample:feature-navtree"))
    implementation(project(":sample:feature-bottomnav"))
    implementation(project(":sample:feature-settings"))
    implementation(project(":sample:feature-details"))
    implementation(project(":sample:feature-common"))
    implementation(project(":sample:feature-welcome:api"))
    implementation(project(":sample:feature-home:api"))
    implementation(project(":sample:feature-nested:api"))
    implementation(project(":sample:feature-dialogs:api"))
    implementation(project(":sample:feature-navtree:api"))
    implementation(project(":sample:feature-bottomnav:api"))
    implementation(project(":sample:feature-settings:api"))
    implementation(project(":sample:feature-details:api"))
    implementation(project(":compose-navigator"))

    // Test dependencies
    androidTestImplementation("androidx.compose.ui:ui-test-junit4:$composeVersion")
    debugImplementation("androidx.compose.ui:ui-test-manifest:$composeVersion")
    testImplementation("junit:junit:$junitVersion")
    testImplementation("org.junit.jupiter:junit-jupiter:$jupiterVersion")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:$jupiterVersion")
    testImplementation("com.google.truth:truth:$truthVersion")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.0")
}

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
    compileSdk = 33

    defaultConfig {
        minSdk = 21
        targetSdk = 33

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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }

    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.composeCompiler.get()
    }

    buildFeatures {
        compose = true
    }

    namespace = "com.roudikk.navigator"
}

dependencies {
    // Compose
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
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    kotlinOptions.freeCompilerArgs += "-opt-in=kotlin.RequiresOptIn"
}

allprojects {
    plugins.withId("com.vanniktech.maven.publish") {
        mavenPublish {
            sonatypeHost = com.vanniktech.maven.publish.SonatypeHost.S01
        }
    }
}

tasks.withType<Sign>().configureEach {
    onlyIf { !project.version.toString().endsWith("SNAPSHOT") }
}

signing {
    val signingKeyId: String? by project
    val signingKey: String? by project
    val signingPassword: String? by project
    useInMemoryPgpKeys(signingKeyId, signingKey, signingPassword)
}

publishing {
    repositories {
        maven {
            val releasesRepoUrl = "$buildDir/repos/releases"
            val snapshotsRepoUrl = "$buildDir/repos/snapshots"
            setUrl(
                if ((version.toString()).endsWith("SNAPSHOT")) {
                    snapshotsRepoUrl
                } else {
                    releasesRepoUrl
                }
            )
        }
    }
}

detekt {
    autoCorrect = true
    buildUponDefaultConfig = true
    allRules = false
}

tasks.withType<Detekt>().configureEach {
    jvmTarget = "1.8"
}

tasks.withType<DetektCreateBaselineTask>().configureEach {
    jvmTarget = "1.8"
}

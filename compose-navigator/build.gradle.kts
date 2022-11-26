@file:Suppress("UnstableApiUsage")

import io.gitlab.arturbosch.detekt.Detekt
import io.gitlab.arturbosch.detekt.DetektCreateBaselineTask

val composeVersion = "1.3.1"
val composeCompilerVersion = "1.3.2"
val composeActivityVersion = "1.6.1"
val kotlinCoroutinesVersion = "1.6.4"
val kotlinVersion = "1.7.20"
val junit4Version = "4.13.2"
val junit5Version = "5.8.2"
val truthVersion = "1.1.3"
val detektVersion = "1.21.0"
val viewModelVersion = "2.5.1"

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
        kotlinCompilerExtensionVersion = composeCompilerVersion
    }

    buildFeatures {
        compose = true
    }

    namespace = "com.roudikk.navigator"
}

dependencies {
    // Compose
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:$viewModelVersion")
    implementation("androidx.compose.ui:ui:$composeVersion")
    implementation("androidx.compose.material:material:$composeVersion")
    implementation("androidx.compose.animation:animation:$composeVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$kotlinCoroutinesVersion")
    implementation("androidx.activity:activity-compose:$composeActivityVersion")

    // Detekt
    detektPlugins("io.gitlab.arturbosch.detekt:detekt-formatting:$detektVersion")

    androidTestImplementation("androidx.compose.ui:ui-test-junit4:$composeVersion")

    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:$kotlinCoroutinesVersion")
    testImplementation("com.google.truth:truth:$truthVersion")
    testImplementation("junit:junit:$junit4Version")
    testImplementation("org.junit.jupiter:junit-jupiter:$junit5Version")
    testImplementation("org.jetbrains.kotlin:kotlin-reflect:$kotlinVersion")

    testRuntimeOnly("org.junit.vintage:junit-vintage-engine:$junit5Version")

    debugImplementation("androidx.compose.ui:ui-test-manifest:$composeVersion")

    androidTestImplementation("androidx.test.ext:junit:1.1.4")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.0")
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
    buildUponDefaultConfig = true // preconfigure defaults
    allRules = false // activate all available (even unstable) rules.
}

tasks.withType<Detekt>().configureEach {
    autoCorrect = true
    reports {
        html.required.set(true) // observe findings in your browser with structure and code snippets
        xml.required.set(true) // checkstyle like format mainly for integrations like Jenkins
        txt.required.set(true) // similar to the console output, contains issue signature to manually edit baseline files
        sarif.required.set(true) // standardized SARIF format (https://sarifweb.azurewebsites.net/) to support integrations with Github Code Scanning
    }
}

tasks.withType<Detekt>().configureEach {
    jvmTarget = "1.8"
}

tasks.withType<DetektCreateBaselineTask>().configureEach {
    jvmTarget = "1.8"
}

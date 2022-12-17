plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("kotlin-parcelize")
}

android {
    namespace = "com.roudikk.navigator.sample.feature.home"
    compileSdk = 33

    defaultConfig {
        minSdk = 24
        targetSdk = 33
    }

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.3.2"
    }
}

dependencies {
    implementation(project(":sample:feature-common"))
    implementation(project(":sample:feature-home:api"))
    implementation(project(":sample:feature-details:api"))
    implementation(project(":sample:feature-dialogs:api"))
    implementation(project(":sample:feature-settings:api"))
    implementation(project(":compose-navigator"))
}

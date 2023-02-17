plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("kotlin-parcelize")
}

android {
    namespace = "com.roudikk.guia.sample.feature.welcome.api"
    compileSdk = 33

    defaultConfig {
        minSdk = 24
        targetSdk = 33
    }
}

dependencies {
    implementation(project(":sample:feature-common"))
    implementation(project(":guia"))
}
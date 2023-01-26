plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("kotlin-parcelize")
}

android {
    namespace = "com.roudikk.guia.sample.feature.bottomnav"
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
    implementation(project(":sample:feature-bottomnav:navigation"))
    implementation(project(":sample:feature-home:navigation"))
    implementation(project(":sample:feature-nested:navigation"))
    implementation(project(":sample:feature-dialogs:navigation"))
    implementation(project(":sample:feature-details:navigation"))
    implementation(project(":sample:feature-custom:navigation"))
    implementation(project(":guia"))
}

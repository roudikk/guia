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

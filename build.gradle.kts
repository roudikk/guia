

buildscript {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }

    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.6.10")
        classpath("com.android.tools.build:gradle:7.2.0-alpha07")
        classpath("de.mannodermaus.gradle.plugins:android-junit5:1.8.0.0")
        classpath("com.google.gms:google-services:4.3.10")
        classpath("com.google.firebase:firebase-crashlytics-gradle:2.8.1")
        classpath("com.vanniktech:gradle-maven-publish-plugin:0.18.0")
        classpath("org.jetbrains.dokka:dokka-gradle-plugin:1.6.10")
    }
}


allprojects {
    repositories {
        google()
        mavenCentral()
        maven(url = "https://androidx.dev/snapshots/builds/8055229/artifacts/repository")
    }
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}

plugins {
    kotlin("jvm")
}

dependencies {
    implementation("com.google.devtools.ksp:symbol-processing-api:1.7.20-1.0.7")
    implementation(project(":compose-navigator"))
}

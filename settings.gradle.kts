pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
    }
}

rootProject.name = "guia"

include(":sample:app")
include(":guia")
include(":sample:feature-welcome")
include(":sample:feature-home")
include(":sample:feature-nested")
include(":sample:feature-dialogs")
include(":sample:feature-bottomnav")
include(":sample:feature-details")
include(":sample:feature-settings")
include(":sample:feature-custom")
include(":sample:feature-welcome:navigation")
include(":sample:feature-home:navigation")
include(":sample:feature-nested:navigation")
include(":sample:feature-dialogs:navigation")
include(":sample:feature-bottomnav:navigation")
include(":sample:feature-details:navigation")
include(":sample:feature-settings:navigation")
include(":sample:feature-custom:navigation")
include(":sample:feature-common")

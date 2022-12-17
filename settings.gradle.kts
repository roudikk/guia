pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
    }
}

rootProject.name = "compose-navigator"

include(":sample:app")
include(":compose-navigator")
include(":sample:feature-welcome")
include(":sample:feature-home")
include(":sample:feature-nested")
include(":sample:feature-dialogs")
include(":sample:feature-navtree")
include(":sample:feature-bottomnav")
include(":sample:feature-details")
include(":sample:feature-settings")
include(":sample:feature-welcome:api")
include(":sample:feature-home:api")
include(":sample:feature-nested:api")
include(":sample:feature-dialogs:api")
include(":sample:feature-navtree:api")
include(":sample:feature-bottomnav:api")
include(":sample:feature-details:api")
include(":sample:feature-settings:api")
include(":sample:feature-common")

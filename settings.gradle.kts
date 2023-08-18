pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "3rd-android-fitnessloT"
include(":app")
include(":core")
include(":feature_auth")
include(":feature_auth:auth_presentation")
include(":feature_auth:auth_domain")
include(":feature_auth:auth_data")

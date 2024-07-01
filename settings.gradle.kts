@file:Suppress("UnstableApiUsage")

include(":core:common")


include(":core:data")


include(":core:datastore")


pluginManagement {
    includeBuild("build-logic")
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

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

rootProject.name = "musicplayer"

include(":app")

include(":core:database")

include(":core:model")

include(":core:resources")

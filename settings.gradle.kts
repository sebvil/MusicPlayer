@file:Suppress("UnstableApiUsage")

include(":core:ui")


include(":core:designsystems")

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

include(":core:database")

include(":core:model")

include(":core:resources")

include(":core:sync")

include(":core:playback")

include(":core:common-test")

include(":core:data-test")

include(":core:common")

include(":core:data")

include(":core:datastore")

include(":app")

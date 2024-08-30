@file:Suppress("UnstableApiUsage")

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

include(":core:services-test")

include(":core:services")

include(":core:ui")

include(":core:designsystems")

include(":features:album:details")

include(":features:album:list")

include(":features:album:menu")

include(":features:artist:details")

include(":features:artist:list")

include(":features:artist:menu")

include(":features:genre:details")

include(":features:genre:list")

include(":features:genre:menu")

include(":features:playlist:details")

include(":features:playlist:list")

include(":features:playlist:menu")

include(":features:playlist:tracksearch")

include(":features:home")

include(":features:sort")

include(":features:main")

include(":features:navigation")

include(":features:queue")

include(":features:search")

include(":features:player")

include(":features:track:list")

include(":features:track:menu")

include(":features:registry")

include(":features:api")

include(":features:artistsmenu")

include(":features:test")

include(":core:ui-test")

include(":tooling")

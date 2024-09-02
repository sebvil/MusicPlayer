plugins {
    alias(libs.plugins.musicplayer.android.application)
    alias(libs.plugins.kover)
}

android {
    namespace = "com.sebastianvm.musicplayer"

    defaultConfig {
        applicationId = "com.sebastianvm.musicplayer"
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
            signingConfig = signingConfigs.getByName("debug")
        }
    }

    kotlinOptions { freeCompilerArgs += "-opt-in=kotlin.RequiresOptIn" }

    @Suppress("UnstableApiUsage")
    testOptions {
        unitTests {
            isIncludeAndroidResources = true
            all { it.useJUnitPlatform() }
        }
    }
}

dependencies {
    implementation(projects.core.database)
    implementation(projects.core.datastore)
    implementation(projects.core.data)
    implementation(projects.core.common)
    implementation(projects.core.playback)
    implementation(projects.core.sync)
    implementation(projects.core.services)

    implementation(projects.features.album.details)
    implementation(projects.features.album.list)
    implementation(projects.features.album.menu)
    implementation(projects.features.artist.details)
    implementation(projects.features.artist.list)
    implementation(projects.features.artist.menu)
    implementation(projects.features.artistsmenu)
    implementation(projects.features.genre.details)
    implementation(projects.features.genre.list)
    implementation(projects.features.genre.menu)
    implementation(projects.features.playlist.details)
    implementation(projects.features.playlist.list)
    implementation(projects.features.playlist.menu)
    implementation(projects.features.playlist.tracksearch)
    implementation(projects.features.track.list)
    implementation(projects.features.track.menu)
    implementation(projects.features.home)
    implementation(projects.features.sort)
    implementation(projects.features.main)
    implementation(projects.features.navigation)
    implementation(projects.features.queue)
    implementation(projects.features.player)
    implementation(projects.features.search)
    implementation(projects.features.api)
    implementation(projects.features.registry)

    // Kotlin
    implementation(libs.core.ktx)

    // endregion
    implementation(libs.androidx.work.runtime.ktx)
}

kover {
    reports {
        filters {
            includes {
                // inclusion rules - classes only those that will be present in reports
                classes("*ViewModel")
            }
        }
    }
}

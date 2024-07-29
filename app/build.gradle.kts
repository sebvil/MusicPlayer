plugins {
    alias(libs.plugins.musicplayer.android.application)
    alias(libs.plugins.compose.compiler)
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
                getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            signingConfig = signingConfigs.getByName("debug")
        }
    }

    kotlinOptions { freeCompilerArgs += "-opt-in=kotlin.RequiresOptIn" }

    buildFeatures { compose = true }

    @Suppress("UnstableApiUsage")
    testOptions {
        unitTests {
            isIncludeAndroidResources = true
            all { it.useJUnitPlatform() }
        }
    }
}

dependencies {
    implementation(projects.core.model)
    implementation(projects.core.database)
    implementation(projects.core.resources)
    implementation(projects.core.datastore)
    implementation(projects.core.data)
    implementation(projects.core.common)
    implementation(projects.core.playback)
    implementation(projects.core.sync)
    implementation(projects.core.designsystems)
    implementation(projects.core.ui)
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

    // Lifecycle
    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.lifecycle.runtime.ktx)
    implementation(libs.lifecycle.viewmodel.compose)
    implementation(libs.lifecycle.runtime.compose)

    // Coil
    implementation(libs.coil)

    // region Jetpack Compose

    // Integration with activities
    implementation(libs.activity.compose)

    // Material components
    implementation(libs.compose.material3)
    implementation(libs.material3.window.size)
    implementation(libs.compose.material.icons.extended)
    // UI + tooling
    implementation(libs.compose.ui.tooling)
    implementation(libs.compose.ui)

    // Accompanist
    implementation(libs.accompanist.permissions)

    // Coil
    implementation(libs.coil.compose)

    implementation(libs.reorderable)

    // endregion

    implementation(libs.androidx.work.runtime.ktx)

    implementation(libs.kotlinx.collections.immutable)

    // Testing
    testImplementation(libs.bundles.testing)
    testImplementation(projects.core.dataTest)
    testImplementation(projects.core.commonTest)
    testImplementation(projects.core.servicesTest)
}

kover {
    reports {
        filters {
            includes {
                // inclusion rules - classes only those that will be present in reports
                classes("*StateHolder", "*ViewModel")
            }
        }
    }
}

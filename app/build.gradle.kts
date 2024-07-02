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

    // Kotlin
    implementation(libs.core.ktx)
    implementation(libs.vectordrawable)

    // Lifecycle
    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.lifecycle.runtime.ktx)
    implementation(libs.lifecycle.viewmodel.compose)
    implementation(libs.lifecycle.runtime.compose)

    // Coil
    implementation(libs.coil)

    // Start Jetpack Compose

    // Integration with activities
    implementation(libs.activity.compose)

    // Material components
    implementation(libs.compose.material3)
    implementation(libs.material3.window.size)
    implementation(libs.androidx.material.icons.extended)
    // UI + tooling
    implementation(libs.androidx.ui.tooling)
    implementation(libs.androidx.ui)

    // Accompanist
    implementation(libs.accompanist.permissions)

    // Coil
    implementation(libs.coil.compose)

    implementation(libs.reorderable)

    // End Jetpack Compose

    implementation(libs.androidx.work.runtime.ktx)

    // Media 3
    implementation(libs.media3.exoplayer)
    implementation(libs.media3.session)

    // Kotlin serialization
    implementation(libs.kotlinx.collections.immutable)

    implementation(libs.kotlinx.coroutines.guava)

    // Testing
    testImplementation(libs.bundles.testing)
    testImplementation(projects.core.dataTest)
    testImplementation(projects.core.commonTest)
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

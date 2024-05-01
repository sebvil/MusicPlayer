plugins {
    alias(libs.plugins.com.android.application)
    alias(libs.plugins.org.jetbrains.kotlin.android)
    alias(libs.plugins.org.jetbrains.kotlin.plugin.serialization)
    alias(libs.plugins.com.google.devtools.ksp)
    alias(libs.plugins.org.jetbrains.kotlin.parcelize)
    alias(libs.plugins.detekt)
}

android {
    namespace = "com.sebastianvm.musicplayer"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.sebastianvm.musicplayer"
        minSdk = 25
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {

        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
        freeCompilerArgs += "-opt-in=kotlin.RequiresOptIn"
    }

    buildFeatures {
        compose = true
        viewBinding = true
    }

    @Suppress("UnstableApiUsage")
    testOptions {
        unitTests {
            isIncludeAndroidResources = true
            all {
                it.useJUnitPlatform()
            }
        }
    }

    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.compose.compiler.get()
    }

    ksp {
        arg("room.generateKotlin", "true")
    }
}

dependencies {
    // Kotlin
    implementation(libs.core.ktx)
    implementation(libs.vectordrawable)
    implementation(libs.recyclerview)

    // Room
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    ksp(libs.androidx.room.compiler)

    // Lifecycle
    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.lifecycle.runtime.ktx)
    implementation(libs.lifecycle.viewmodel.compose)
    implementation(libs.lifecycle.runtime.compose)

    // DataStore
    implementation(libs.datastore.preferences)
    implementation(libs.datastore)

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

    // Glide
    implementation(libs.coil.compose)

    implementation(libs.constraintlayout.compose)

    // End Jetpack Compose

    // Media 3
    implementation(libs.media3.exoplayer)
    implementation(libs.media3.session)

    // Kotlin serialization
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.kotlinx.collections.immutable)

    implementation(libs.kotlinx.coroutines.guava)

    // TESTING

    // Testing
    testImplementation(libs.bundles.testing)

    implementation(project(":fakegen"))
    kspTest(project(":fakegen"))

    detektPlugins(libs.detekt.ktlint)
    detektPlugins(libs.detekt.compose)
}

detekt {
    // Applies the config files on top of detekt"s default config file. `false` by default.
    buildUponDefaultConfig = true

    // Turns on all the rules. `false` by default.
    allRules = false
    enableCompilerPlugin.set(true)
    config.setFrom(file("../config/detekt/detekt.yml"))
    autoCorrect = true
}

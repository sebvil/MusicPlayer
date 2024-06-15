plugins {
    alias(libs.plugins.com.android.application)
    alias(libs.plugins.org.jetbrains.kotlin.android)
    alias(libs.plugins.org.jetbrains.kotlin.plugin.serialization)
    alias(libs.plugins.com.google.devtools.ksp)
    alias(libs.plugins.detekt)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.kover)
    alias(libs.plugins.ktfmt)
}

android {
    namespace = "com.sebastianvm.musicplayer"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.sebastianvm.musicplayer"
        minSdk = 25
        targetSdk = 34
        targetSdkPreview = "VanillaIceCream"
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
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("debug")
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

    buildFeatures { compose = true }

    @Suppress("UnstableApiUsage")
    testOptions {
        unitTests {
            isIncludeAndroidResources = true
            all { it.useJUnitPlatform() }
        }
    }

    ksp { arg("room.generateKotlin", "true") }
}

dependencies {
    // Kotlin
    implementation(libs.core.ktx)
    implementation(libs.vectordrawable)

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

    // Coil
    implementation(libs.coil.compose)

    implementation(libs.reorderable)

    // End Jetpack Compose

    implementation(libs.androidx.work.runtime.ktx)

    // Media 3
    implementation(libs.media3.exoplayer)
    implementation(libs.media3.session)

    // Kotlin serialization
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.kotlinx.collections.immutable)

    implementation(libs.kotlinx.coroutines.guava)

    // Testing
    testImplementation(libs.bundles.testing)

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

ktfmt {
    kotlinLangStyle()

    manageTrailingCommas.set(true)
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

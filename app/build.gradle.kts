@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
    alias(libs.plugins.com.android.application)
    alias(libs.plugins.org.jetbrains.kotlin.android)
    alias(libs.plugins.org.jetbrains.kotlin.plugin.serialization)
    alias(libs.plugins.org.jetbrains.kotlin.kapt)
    alias(libs.plugins.com.google.dagger.hilt.android)
    alias(libs.plugins.com.google.devtools.ksp)
    alias(libs.plugins.org.jetbrains.kotlin.parcelize)
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

    testOptions {
        unitTests {
            isIncludeAndroidResources = true
        }
    }


    composeOptions {
        kotlinCompilerExtensionVersion = "1.4.8"
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

    // Hilt
    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)

    // Coil
    implementation(libs.coil)


    // Start Jetpack Compose
    // Navigation
    implementation(libs.navigation.compose)

    // Integration with activities
    implementation(libs.activity.compose)
    // Hilt navigation
    implementation(libs.hilt.navigation.compose)

    // Material components
    implementation(libs.compose.material3)
    implementation(libs.material3.window.size)
    implementation(libs.androidx.material.icons.extended)
    // UI + tooling
    implementation(libs.androidx.ui.tooling)
    implementation(libs.androidx.ui)

    // Accompanist
    implementation(libs.accompanist.navigation.material)
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

    // Junit
    testImplementation(libs.junit)

    // Coroutines tests
    testImplementation(libs.kotlinx.coroutines.test)

    testImplementation(libs.test.core.ktx)
    testImplementation(libs.core)

    // Mockk
    testImplementation(libs.mockk)

    testImplementation(libs.kotlin.test)

    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.ui.test.manifest)
    androidTestImplementation(libs.test.core)

}

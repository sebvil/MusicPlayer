plugins {
    alias(libs.plugins.musicplayer.android.library)

    alias(libs.plugins.compose.compiler)
}

android {
    namespace = "com.sebastianvm.musicplayer.core.servicestest"

    defaultConfig { testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner" }
    buildFeatures { compose = true }
}

dependencies {
    implementation(projects.core.services)
    implementation(projects.core.model)
    implementation(projects.core.commonTest)

    implementation(libs.kotlinx.coroutines)
    implementation(libs.compose.ui)
}

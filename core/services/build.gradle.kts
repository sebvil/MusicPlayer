plugins {
    alias(libs.plugins.musicplayer.android.library)
    alias(libs.plugins.compose.compiler)
}

android {
    namespace = "com.sebastianvm.musicplayer.core.services"

    defaultConfig { testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner" }

    buildFeatures { compose = true }
}

dependencies {
    implementation(projects.core.model)
    implementation(projects.core.datastore)
    implementation(projects.core.data)
    implementation(libs.kotlinx.coroutines)

    implementation(libs.compose.ui.tooling)
    implementation(libs.compose.ui)
}

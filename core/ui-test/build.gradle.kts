plugins {
    alias(libs.plugins.musicplayer.android.library)

    alias(libs.plugins.compose.compiler)
}

android {
    namespace = "com.sebastianvm.musicplayer.core.uitest"

    defaultConfig { testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner" }
    buildFeatures { compose = true }
}

dependencies {
    implementation(projects.core.designsystems)
    implementation(projects.core.ui)
    implementation(projects.core.model)
    implementation(projects.core.commonTest)

    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.kotlinx.coroutines)
    implementation(libs.compose.runtime)
    implementation(libs.compose.ui)
}

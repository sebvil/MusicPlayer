plugins {
    alias(libs.plugins.musicplayer.android.library)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.parcelize)
}

android {
    namespace = "com.sebastianvm.musicplayer.core.ui"

    buildFeatures { compose = true }
}

dependencies {
    implementation(projects.core.model)
    implementation(projects.core.resources)
    implementation(projects.core.designsystems)
    implementation(projects.core.sync)

    implementation(libs.compose.runtime)
    implementation(libs.compose.material3)
    implementation(libs.compose.material.icons.extended)
    implementation(libs.compose.ui.tooling)
    implementation(libs.compose.ui)

    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.kotlinx.collections.immutable)
    implementation(libs.coil)
    implementation(libs.coil.compose)

    implementation(libs.androidx.work.runtime.ktx)
    implementation(libs.accompanist.permissions)
}

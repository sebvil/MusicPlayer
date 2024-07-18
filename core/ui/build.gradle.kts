plugins {
    alias(libs.plugins.musicplayer.android.library)
    alias(libs.plugins.compose.compiler)
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
    implementation(projects.core.services)

    implementation(libs.compose.material3)
    implementation(libs.compose.material.icons.extended)
    implementation(libs.compose.ui.tooling)
    implementation(libs.compose.ui)

    implementation(libs.kotlinx.collections.immutable)
    implementation(libs.coil)
    implementation(libs.coil.compose)

    implementation(libs.androidx.work.runtime.ktx)
    implementation(libs.accompanist.permissions)
}

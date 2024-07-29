plugins {
    alias(libs.plugins.musicplayer.android.library)
    alias(libs.plugins.compose.compiler)
}

android { namespace = "com.sebastianvm.musicplayer.features.test" }

dependencies {
    implementation(projects.core.model)
    implementation(projects.core.ui)
    implementation(projects.core.uiTest)
    implementation(projects.core.services)
    implementation(projects.features.registry)
    implementation(projects.features.api)

    implementation(libs.kotlinx.coroutines)
    implementation(libs.compose.ui)
}

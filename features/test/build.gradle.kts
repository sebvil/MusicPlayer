plugins { alias(libs.plugins.musicplayer.android.feature) }

android { namespace = "com.sebastianvm.musicplayer.features.test" }

dependencies {
    implementation(projects.core.model)
    implementation(projects.core.uiTest)
    implementation(projects.features.registry)
    implementation(projects.features.api)

    implementation(libs.kotlinx.coroutines)
    implementation(libs.compose.ui)
}

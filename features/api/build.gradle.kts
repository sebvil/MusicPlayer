plugins { alias(libs.plugins.musicplayer.android.library) }

android { namespace = "com.sebastianvm.musicplayer.features.api" }

dependencies {
    implementation(projects.core.model)
    implementation(projects.core.ui)
    implementation(projects.features.registry)

    implementation(libs.kotlinx.coroutines)
}

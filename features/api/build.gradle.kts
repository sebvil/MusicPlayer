plugins { alias(libs.plugins.musicplayer.android.library) }

android { namespace = "com.sebastianvm.musicplayer.features.api" }

dependencies {
    implementation(projects.core.ui)
    implementation(projects.core.model)
}

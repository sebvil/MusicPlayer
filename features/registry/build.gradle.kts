plugins { alias(libs.plugins.musicplayer.android.library) }


android { namespace = "com.sebastianvm.musicplayer.features.registry" }


dependencies {
    implementation(projects.core.ui)
    implementation(libs.kotlinx.coroutines)
}
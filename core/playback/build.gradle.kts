plugins { alias(libs.plugins.musicplayer.android.library) }

android {
    namespace = "com.sebastianvm.musicplayer.core.playback"

    defaultConfig { testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner" }
}

dependencies {
    implementation(projects.core.model)
    implementation(projects.core.data)
    implementation(projects.core.common)
    implementation(projects.core.resources)
    implementation(projects.core.datastore)
    implementation(projects.core.database)

    implementation(libs.media3.exoplayer)
    implementation(libs.media3.session)

    implementation(libs.androidx.annotation)
    implementation(libs.kotlinx.coroutines)
    implementation(libs.kotlinx.coroutines.guava)
    implementation(libs.core.ktx)
    implementation(libs.vectordrawable)
}

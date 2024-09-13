plugins { alias(libs.plugins.musicplayer.android.library) }

android {
    namespace = "com.sebastianvm.musicplayer.core.data"

    defaultConfig { testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner" }
}

dependencies {
    implementation(projects.core.model)
    implementation(projects.core.database)
    implementation(projects.core.datastore)
    implementation(projects.core.common)
    implementation(projects.core.resources)

    implementation(libs.kotlinx.coroutines)
    implementation(libs.androidx.annotation)
    implementation(libs.kotlinx.collections.immutable)
    implementation(libs.datastore)
}

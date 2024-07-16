plugins { alias(libs.plugins.musicplayer.android.library) }

android {
    namespace = "com.sebastianvm.musicplayer.core.sync"

    defaultConfig { testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner" }
}

dependencies {
    implementation(projects.core.data)
    implementation(projects.core.resources)

    implementation(libs.kotlinx.coroutines)
    implementation(libs.core.ktx)
    implementation(libs.androidx.work.runtime.ktx)
}

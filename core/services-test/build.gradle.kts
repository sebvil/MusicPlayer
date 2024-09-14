plugins { alias(libs.plugins.musicplayer.android.library) }

android {
    namespace = "com.sebastianvm.musicplayer.core.servicestest"

    defaultConfig { testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner" }
}

dependencies {
    implementation(projects.core.model)
    implementation(projects.core.commonTest)
    implementation(projects.core.playback)

    implementation(libs.kotlinx.coroutines)
}

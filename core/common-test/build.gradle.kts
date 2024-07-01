plugins { alias(libs.plugins.musicplayer.android.library) }

android {
    namespace = "com.sebastianvm.musicplayer.core.commontest"

    defaultConfig { testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner" }
}

dependencies {
    implementation(projects.core.model)
    implementation(projects.core.datastore)

    implementation(libs.fixture.monkey.kotest)
    implementation(libs.fixture.monkey)
}

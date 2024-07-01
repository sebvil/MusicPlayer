plugins { alias(libs.plugins.musicplayer.android.library) }

android {
    namespace = "com.sebastianvm.musicplayer.core.datatest"

    defaultConfig { testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner" }
}

dependencies {
    implementation(projects.core.data)
    implementation(projects.core.model)
    implementation(projects.core.common)
    implementation(projects.core.database)
    implementation(projects.core.commonTest)
    implementation(projects.core.datastore)

    implementation(libs.kotlinx.coroutines)
}

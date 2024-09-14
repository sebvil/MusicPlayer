plugins {
    alias(libs.plugins.musicplayer.android.library)
    alias(libs.plugins.com.google.devtools.ksp)
}

android {
    namespace = "com.sebastianvm.musicplayer.core.database"

    defaultConfig { testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner" }

    ksp { arg("room.generateKotlin", "true") }
}

dependencies {
    implementation(projects.core.common)

    // Room
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)

    ksp(libs.androidx.room.compiler)
}

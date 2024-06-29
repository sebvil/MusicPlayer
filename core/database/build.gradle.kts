plugins {
    alias(libs.plugins.musicplayer.android.library)
    alias(libs.plugins.com.google.devtools.ksp)
    alias(libs.plugins.ktfmt)
}

android {
    namespace = "com.sebastianvm.musicplayer.core.database"

    defaultConfig { testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner" }

    ksp { arg("room.generateKotlin", "true") }
}

dependencies {
    // Room
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    ksp(libs.androidx.room.compiler)
}

ktfmt {
    kotlinLangStyle()

    manageTrailingCommas.set(true)
}

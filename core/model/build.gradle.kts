plugins {
    alias(libs.plugins.musicplayer.android.library)
    alias(libs.plugins.org.jetbrains.kotlin.plugin.serialization)
    alias(libs.plugins.parcelize)
}

android { namespace = "com.sebastianvm.musicplayer.core.model" }


dependencies {
    implementation(libs.kotlinx.serialization.json)
}

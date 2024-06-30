plugins {
    alias(libs.plugins.musicplayer.jvm.library)
    alias(libs.plugins.org.jetbrains.kotlin.plugin.serialization)
}

dependencies {
    implementation(libs.kotlinx.serialization.json)
}

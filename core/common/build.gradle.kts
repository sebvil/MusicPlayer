import com.sebastianvm.musicplayer.convention.configureDi

plugins {
    alias(libs.plugins.musicplayer.android.library)
    alias(libs.plugins.com.google.devtools.ksp)
}
android {
    namespace = "com.sebastianvm.musicplayer.core.common"
}
dependencies {
    implementation(libs.kotlinx.coroutines)
}


configureDi()
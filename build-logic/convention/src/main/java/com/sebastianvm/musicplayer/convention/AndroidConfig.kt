package com.sebastianvm.musicplayer.convention

import com.android.build.api.dsl.CommonExtension
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.dsl.KotlinAndroidProjectExtension

internal fun Project.configureAndroid(
    commonExtension: CommonExtension<*, *, *, *, *, *>,
) {
    commonExtension.apply {
        compileSdk = 34

        defaultConfig { minSdk = 25 }

        compileOptions {
            sourceCompatibility = Constants.JAVA_VERSION
            targetCompatibility = Constants.JAVA_VERSION
        }
    }

    configureKotlin<KotlinAndroidProjectExtension>()
}

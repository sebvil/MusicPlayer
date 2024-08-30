package com.sebastianvm.musicplayer.convention

import com.android.build.api.dsl.CommonExtension
import com.android.build.gradle.LibraryExtension
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.dsl.KotlinAndroidProjectExtension

internal fun Project.configureAndroid(commonExtension: CommonExtension<*, *, *, *, *, *>) {
    commonExtension.apply {
        compileSdk = 35

        defaultConfig { minSdk = 24 }
    }

    configureKotlin<KotlinAndroidProjectExtension>()
}

internal fun Project.configureAndroidLib() {
    apply(plugin = "com.android.library")
    apply(plugin = "org.jetbrains.kotlin.android")

    configureDetekt()
    configureKtfmt()

    extensions.configure<LibraryExtension> { configureAndroid(this) }
}

package com.sebastianvm.musicplayer.convention

import com.android.build.gradle.LibraryExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.dsl.KotlinAndroidProjectExtension

class AndroidLibraryConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            apply(plugin = "com.android.library")
            apply(plugin = "org.jetbrains.kotlin.android")

            extensions.configure<LibraryExtension> {
                compileSdk = 34

                defaultConfig { minSdk = 25 }

                compileOptions {
                    sourceCompatibility = Constants.JAVA_VERSION
                    targetCompatibility = Constants.JAVA_VERSION
                }

                configureKotlin<KotlinAndroidProjectExtension>()
            }
        }
    }
}

package com.sebastianvm.musicplayer.convention

import com.android.build.gradle.LibraryExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies

class ComposeConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            configureAndroidLib()
            apply(plugin = "org.jetbrains.kotlin.plugin.compose")

            extensions.configure<LibraryExtension> {
                buildFeatures { compose = true }

                @Suppress("UnstableApiUsage")
                testOptions {
                    unitTests {
                        isIncludeAndroidResources = true
                        all { it.useJUnitPlatform() }
                    }
                }
            }

            dependencies {
                implementation(project(":core:designsystems"))
                implementation(project(":core:ui"))
                implementation(project(":core:services"))
                implementation(project(":features:api"))
                implementation(project(":features:registry"))

                implementation(libs.lib("compose.material3"))
                implementation(libs.lib("compose.runtime"))
                implementation(libs.lib("compose.ui.tooling"))
                implementation(libs.lib("compose.ui"))

                testImplementation(libs.bundle("testing"))
                testImplementation(project(":core:common-test"))
                testImplementation(project(":core:data-test"))
                testImplementation(project(":core:services-test"))
                testImplementation(project(":core:ui-test"))
                testImplementation(project(":features:test"))
            }
        }
    }
}

package com.sebastianvm.musicplayer.convention

import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.dsl.KotlinAndroidProjectExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinBaseExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension

internal inline fun <reified T : KotlinBaseExtension> Project.configureKotlin() =
    configure<T> {
        when (this) {
            is KotlinAndroidProjectExtension -> compilerOptions
            is KotlinJvmProjectExtension -> compilerOptions
            else -> error("Unsupported extension type: ${T::class.java}")
        }.apply {
            jvmToolchain(17)
            allWarningsAsErrors.set(false)
        }
    }

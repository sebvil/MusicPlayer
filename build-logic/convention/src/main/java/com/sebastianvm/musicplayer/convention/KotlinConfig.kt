package com.sebastianvm.musicplayer.convention

import org.gradle.api.Project
import org.gradle.kotlin.dsl.assign
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.dsl.KotlinAndroidProjectExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinTopLevelExtension

internal inline fun <reified T : KotlinTopLevelExtension> Project.configureKotlin() =
    configure<T> {
        when (this) {
            is KotlinAndroidProjectExtension -> compilerOptions
            is KotlinJvmProjectExtension -> compilerOptions
            else -> error("Unsupported extension type: ${T::class.java}")
        }.apply {
            jvmTarget = Constants.JVM_TARGET
            allWarningsAsErrors = true
        }
    }

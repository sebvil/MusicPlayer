package com.sebastianvm.musicplayer.convention

import com.google.devtools.ksp.gradle.KspExtension
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies

fun Project.configureDi() {

    apply(plugin = "com.google.devtools.ksp")
    dependencies {
        // Koin
        implementation(libs.lib("koin.android"))
        // Koin Annotations
        implementation(libs.lib("koin.annotations"))
        // Koin Annotations KSP Compiler
        ksp(libs.lib("koin.ksp.compiler"))
    }

    configure<KspExtension> { arg("KOIN_CONFIG_CHECK", "true") }
}

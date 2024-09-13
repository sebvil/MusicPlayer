package com.sebastianvm.musicplayer.convention

import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.dependencies

fun Project.configureDi() {

    apply(plugin = "com.google.devtools.ksp")
    dependencies {
        ksp(libs.lib("kotlin.inject.compiler.ksp"))
        implementation(libs.lib("kotlin.inject.runtime"))
        implementation(project(":di-annotations"))
    }
}

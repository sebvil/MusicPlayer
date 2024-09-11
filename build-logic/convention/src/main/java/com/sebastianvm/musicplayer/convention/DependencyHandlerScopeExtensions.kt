package com.sebastianvm.musicplayer.convention

import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.provider.Provider
import org.gradle.kotlin.dsl.DependencyHandlerScope

fun DependencyHandlerScope.testImplementation(dependency: Provider<*>) {
    "testImplementation"(dependency)
}

fun DependencyHandlerScope.testImplementation(project: Project) {
    "testImplementation"(project)
}

fun DependencyHandlerScope.implementation(dependency: Provider<*>) {
    "implementation"(dependency)
}

fun DependencyHandlerScope.implementation(project: Project) {
    "implementation"(project)
}

fun DependencyHandlerScope.detektPlugins(libs: VersionCatalog, lib: String) {
    "detektPlugins"(libs.findLibrary(lib).get())
}

fun DependencyHandlerScope.ksp(project: Project) {
    "ksp"(project)
}

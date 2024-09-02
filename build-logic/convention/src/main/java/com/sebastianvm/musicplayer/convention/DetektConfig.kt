package com.sebastianvm.musicplayer.convention

import io.gitlab.arturbosch.detekt.extensions.DetektExtension
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies

internal fun Project.configureDetekt(includeCompose: Boolean = false) {
    apply(plugin = "io.gitlab.arturbosch.detekt")

    if (includeCompose) {
        dependencies { detektPlugins(libs, "detekt.compose") }
    }

    configure<DetektExtension> {
        enableCompilerPlugin.set(true)
        buildUponDefaultConfig = true
        allRules = false
        autoCorrect = true
        if (includeCompose) {
            config.setFrom(
                file("$rootDir/config/detekt/detekt.yml"),
                file("$rootDir/config/detekt/compose-detekt.yml"),
            )
        } else {
            config.setFrom(file("$rootDir/config/detekt/detekt.yml"))
        }
    }
}

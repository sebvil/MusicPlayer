package com.sebastianvm.musicplayer.convention

import org.gradle.api.Plugin
import org.gradle.api.Project

class AndroidLibraryConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) { configureAndroidLib() }
    }
}

import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins { `kotlin-dsl` }

group = "com.sebastianvm.musicplayer.buildlogic"

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

kotlin { compilerOptions { jvmTarget = JvmTarget.JVM_17 } }

dependencies {
    compileOnly(libs.android.gradlePlugin)
    compileOnly(libs.android.tools.common)
    compileOnly(libs.compose.gradlePlugin)
    compileOnly(libs.kotlin.gradlePlugin)
    compileOnly(libs.ksp.gradlePlugin)
    compileOnly(libs.room.gradlePlugin)
}

tasks {
    validatePlugins {
        enableStricterValidation = true
        failOnWarning = true
    }
}

gradlePlugin {
    plugins {
        register("androidApplication") {
            id = "musicplayer.android.application"
            implementationClass =
                "com.sebastianvm.musicplayer.convention.AndroidApplicationConventionPlugin"
        }

        register("androidLibrary") {
            id = "musicplayer.android.library"
            implementationClass =
                "com.sebastianvm.musicplayer.convention.AndroidLibraryConventionPlugin"
        }

        register("jvmLibrary") {
            id = "musicplayer.jvm.library"
            implementationClass =
                "com.sebastianvm.musicplayer.convention.JvmLibraryConventionPlugin"
        }
    }
}

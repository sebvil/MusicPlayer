import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    `kotlin-dsl`
    alias(libs.plugins.detekt)
    alias(libs.plugins.ktfmt)
}

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
    compileOnly(libs.detekt.gradlePlugin)
    compileOnly(libs.ktfmt.gradlePlugin)
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

        register("androidFeature") {
            id = "musicplayer.android.feature"
            implementationClass =
                "com.sebastianvm.musicplayer.convention.AndroidFeatureConventionPlugin"
        }

        register("jvmLibrary") {
            id = "musicplayer.jvm.library"
            implementationClass =
                "com.sebastianvm.musicplayer.convention.JvmLibraryConventionPlugin"
        }
    }
}

detekt {
    // Applies the config files on top of detekt"s default config file. `false` by default.
    buildUponDefaultConfig = true

    // Turns on all the rules. `false` by default.
    allRules = false
    enableCompilerPlugin.set(true)
    config.setFrom(file("../config/detekt/detekt.yml"))
    autoCorrect = true
}

ktfmt {
    kotlinLangStyle()

    manageTrailingCommas.set(true)
}

@Suppress("DSL_SCOPE_VIOLATION", "ForbiddenComment") // TODO: Remove once KTIJ-19369 is fixed
plugins {
    id("java-library")
    alias(libs.plugins.org.jetbrains.kotlin.jvm)
    alias(libs.plugins.detekt)
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

dependencies {
    implementation(libs.ksp.api)

    detektPlugins(libs.detekt.ktlint)
    detektPlugins(libs.detekt.compose)
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

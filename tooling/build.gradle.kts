plugins {
    alias(libs.plugins.musicplayer.jvm.library)
    alias(libs.plugins.org.jetbrains.kotlin.plugin.serialization)
    application
}

application {
    mainClass.set("MainKt") // Adjust the package and class name accordingly
}

// group = "com.sebastianvm.musicplayer"

version = "1.0-SNAPSHOT"

dependencies {
    implementation(libs.kotlin.stdlib)
    testImplementation(kotlin("test"))
    implementation(libs.clikt)
    implementation(libs.ktoml.core)
    implementation(libs.ktoml.file)
}

tasks.test { useJUnitPlatform() }

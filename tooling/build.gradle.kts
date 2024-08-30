plugins {
    alias(libs.plugins.musicplayer.jvm.library)
    alias(libs.plugins.org.jetbrains.kotlin.plugin.serialization)
    application
    id("com.github.johnrengelman.shadow") version "7.1.0" // Check for the latest version
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

tasks.withType<Jar> {
    manifest {
        attributes["Main-Class"] = "MainKt" // Replace with your main class
    }

    duplicatesStrategy = DuplicatesStrategy.EXCLUDE // Options: EXCLUDE, INCLUDE, or WARN
    from({
        configurations.runtimeClasspath
            .get()
            .filter { it.exists() }
            .flatMap {
                if (it.isDirectory) {
                    it as FileCollection
                } else {
                    zipTree(it)
                }
            }
    })
}

tasks.withType<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar> {
    archiveBaseName.set("tooling")
    manifest { attributes["Main-Class"] = "MainKt" }
}

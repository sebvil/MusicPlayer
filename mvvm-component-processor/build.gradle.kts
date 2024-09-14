plugins {
    alias(libs.plugins.musicplayer.jvm.library)
}


dependencies {
    implementation(projects.kspAnnotations)

    implementation(libs.kotlinpoet)
    implementation(libs.kotlinpoet.ksp)
    implementation(libs.symbol.processing.api)
}
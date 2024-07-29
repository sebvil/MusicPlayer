plugins {
    alias(libs.plugins.musicplayer.android.library)
    alias(libs.plugins.compose.compiler)
}

android {
    namespace = "com.sebastianvm.musicplayer.features.playlist.list"

    buildFeatures { compose = true }

    @Suppress("UnstableApiUsage")
    testOptions {
        unitTests {
            isIncludeAndroidResources = true
            all { it.useJUnitPlatform() }
        }
    }
}

dependencies {
    implementation(projects.core.model)
    implementation(projects.core.resources)
    implementation(projects.core.designsystems)
    implementation(projects.core.services)
    implementation(projects.core.data)
    implementation(projects.core.datastore)
    implementation(projects.core.ui)
    implementation(projects.features.api)
    implementation(projects.features.registry)

    implementation(libs.compose.material3)
    implementation(libs.compose.material.icons.extended)
    implementation(libs.compose.ui.tooling)
    implementation(libs.compose.ui)

    implementation(libs.kotlinx.collections.immutable)

    testImplementation(libs.bundles.testing)
    testImplementation(projects.core.dataTest)
    testImplementation(projects.core.commonTest)
    testImplementation(projects.core.servicesTest)
    testImplementation(projects.core.uiTest)
    testImplementation(projects.features.test)
}

plugins { alias(libs.plugins.musicplayer.android.feature) }

android { namespace = "com.sebastianvm.musicplayer.features.search" }

dependencies {
    implementation(projects.core.model)
    implementation(projects.core.resources)
    implementation(projects.core.sync)
    implementation(projects.core.data)

    implementation(libs.androidx.work.runtime.ktx)

    implementation(libs.kotlinx.collections.immutable)

    testImplementation(projects.core.dataTest)
    testImplementation(projects.core.servicesTest)
    testImplementation(projects.core.uiTest)
    testImplementation(projects.features.test)
}

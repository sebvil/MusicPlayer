plugins { alias(libs.plugins.musicplayer.android.feature) }

android { namespace = "com.sebastianvm.musicplayer.features.track.list" }

dependencies {
    implementation(projects.core.model)
    implementation(projects.core.resources)
    implementation(projects.core.data)
    implementation(projects.core.datastore)
    implementation(projects.core.playback)

    implementation(libs.kotlinx.collections.immutable)

    testImplementation(projects.core.dataTest)
    testImplementation(projects.core.servicesTest)
    testImplementation(projects.core.uiTest)
    testImplementation(projects.features.test)
}

plugins { alias(libs.plugins.musicplayer.android.feature) }

android { namespace = "com.sebastianvm.musicplayer.features.album.list" }

dependencies {
    implementation(projects.core.model)
    implementation(projects.core.resources)
    implementation(projects.core.data)
    implementation(projects.core.datastore)

    testImplementation(projects.core.dataTest)
    testImplementation(projects.core.uiTest)
    testImplementation(projects.features.test)
}

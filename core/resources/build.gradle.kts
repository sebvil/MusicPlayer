plugins { alias(libs.plugins.musicplayer.android.library) }

android { namespace = "com.sebastianvm.musicplayer.core.resources" }

dependencies { implementation(libs.core.ktx) }

plugins {
    alias(libs.plugins.musicplayer.android.library)
    alias(libs.plugins.ktfmt)
}

android { namespace = "com.sebastianvm.musicplayer.core.resources" }

dependencies { implementation(libs.core.ktx) }

ktfmt {
    kotlinLangStyle()

    manageTrailingCommas.set(true)
}

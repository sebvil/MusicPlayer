plugins {
    alias(libs.plugins.musicplayer.jvm.library)
    alias(libs.plugins.org.jetbrains.kotlin.plugin.serialization)
    alias(libs.plugins.ktfmt)
}

dependencies { implementation(libs.kotlinx.serialization.json) }

ktfmt {
    kotlinLangStyle()

    manageTrailingCommas.set(true)
}

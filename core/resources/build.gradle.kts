plugins {
    alias(libs.plugins.musicplayer.android.library)
    alias(libs.plugins.detekt)
    alias(libs.plugins.ktfmt)
}

android { namespace = "com.sebastianvm.musicplayer.core.resources" }

dependencies { implementation(libs.core.ktx) }

detekt {
    // Applies the config files on top of detekt"s default config file. `false` by default.
    buildUponDefaultConfig = true

    // Turns on all the rules. `false` by default.
    allRules = false
    enableCompilerPlugin.set(true)
    config.setFrom(file("${rootDir}/config/detekt/detekt.yml"))
    autoCorrect = true
}

ktfmt {
    kotlinLangStyle()

    manageTrailingCommas.set(true)
}

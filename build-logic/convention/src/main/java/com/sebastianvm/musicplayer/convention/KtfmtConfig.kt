package com.sebastianvm.musicplayer.convention

import com.ncorti.ktfmt.gradle.KtfmtExtension
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure

internal fun Project.configureKtfmt() {
    apply(plugin = "com.ncorti.ktfmt.gradle")

    configure<KtfmtExtension> {
        kotlinLangStyle()
        manageTrailingCommas.set(true)
        removeUnusedImports.set(true)
    }
}

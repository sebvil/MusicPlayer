package com.sebastianvm.musicplayer.features.album.details

import io.kotest.core.config.AbstractProjectConfig
import io.kotest.core.extensions.Extension

class ProjectConfig : AbstractProjectConfig() {
    override val coroutineTestScope: Boolean = true

    override fun extensions(): List<Extension> {
        return listOf(CancelChildrenOnFinishExtension)
    }
}

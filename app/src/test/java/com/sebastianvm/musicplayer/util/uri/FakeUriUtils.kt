package com.sebastianvm.musicplayer.util.uri

import io.mockk.CapturingSlot
import io.mockk.every
import io.mockk.mockkObject
import org.junit.rules.TestWatcher
import org.junit.runner.Description

class FakeUriUtilsRule : TestWatcher() {
    override fun starting(description: Description) {
        super.starting(description)
        mockkObject(UriUtils)
        val idSlot = CapturingSlot<Long>()
        every { UriUtils.getAlbumUri(capture(idSlot)) } answers { "$FAKE_ALBUM_PATH/${idSlot.captured}" }
    }

    override fun finished(description: Description) {
        super.finished(description)
    }

    companion object {
        const val FAKE_ALBUM_PATH = "path/to/album"
    }

}


package com.sebastianvm.musicplayer.util

import android.os.Bundle
import android.os.Parcelable
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockkConstructor
import io.mockk.slot

class BundleMock {
    private val bundleMap: MutableMap<String, Any> = mutableMapOf()

    private val key = slot<String>()
    private val parcelableValue = slot<Parcelable>()

    init {
        mockkConstructor(Bundle::class)
        every { anyConstructed<Bundle>().putParcelable(any(), any()) } just Runs
        every { anyConstructed<Bundle>().putParcelable(capture(key), capture(parcelableValue)) } answers {
            bundleMap[key.captured] = parcelableValue.captured
        }
    }

    @Suppress("UNCHECKED_CAST")
    fun <T: Parcelable> addParcelableGetter() {
        every { anyConstructed<Bundle>().getParcelable<T>(capture(key)) } answers { bundleMap[key.captured] as? T }
    }


}

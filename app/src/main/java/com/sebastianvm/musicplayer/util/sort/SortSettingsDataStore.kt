package com.sebastianvm.musicplayer.util.sort

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import androidx.datastore.preferences.protobuf.InvalidProtocolBufferException
import java.io.InputStream
import java.io.OutputStream

@Suppress("BlockingMethodInNonBlockingContext")
object SortSettingsSerializer : Serializer<SortSettings> {
    override val defaultValue: SortSettings = SortSettings.getDefaultInstance()

    override suspend fun readFrom(input: InputStream): SortSettings {
        try {
            return SortSettings.parseFrom(input)
        } catch (exception: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read proto.", exception)
        }
    }

    override suspend fun writeTo(t: SortSettings, output: OutputStream) {
        t.writeTo(output)
    }
}
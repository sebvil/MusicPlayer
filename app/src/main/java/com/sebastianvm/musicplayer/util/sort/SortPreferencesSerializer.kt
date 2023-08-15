package com.sebastianvm.musicplayer.util.sort

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import java.io.InputStream
import java.io.OutputStream

@Suppress("BlockingMethodInNonBlockingContext")
object SortPreferencesSerializer : Serializer<SortPreferences> {

    private val serializer = SortPreferences.serializer()

    override val defaultValue = SortPreferences()

    override suspend fun readFrom(input: InputStream): SortPreferences {
        try {
            return Json.decodeFromString(
                serializer,
                input.readBytes().decodeToString()
            )
        } catch (serialization: SerializationException) {
            throw CorruptionException("Unable to read SortPreferences", serialization)
        }
    }

    override suspend fun writeTo(t: SortPreferences, output: OutputStream) {
        output.write(
            Json.encodeToString(serializer, t)
                .encodeToByteArray()
        )
    }
}

package com.sebastianvm.musicplayer.core.datastore

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import java.io.InputStream
import java.io.OutputStream
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json

internal class KotlinSerializationSerializer<T>(
    override val defaultValue: T,
    private val serializer: KSerializer<T>,
    private val ioDispatcher: CoroutineDispatcher,
) : Serializer<T> {

    override suspend fun readFrom(input: InputStream): T {
        try {
            return Json.decodeFromString(serializer, input.readBytes().decodeToString())
        } catch (serialization: SerializationException) {
            throw CorruptionException("Unable to read SortPreferences", serialization)
        }
    }

    override suspend fun writeTo(t: T, output: OutputStream) {
        withContext(ioDispatcher) {
            output.write(Json.encodeToString(serializer, t).encodeToByteArray())
        }
    }
}

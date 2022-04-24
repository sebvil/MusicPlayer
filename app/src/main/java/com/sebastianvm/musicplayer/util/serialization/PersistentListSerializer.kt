package com.sebastianvm.musicplayer.util.serialization

import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.toPersistentList
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializer
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder


@OptIn(ExperimentalSerializationApi::class)
@Serializer(forClass = PersistentList::class)
object PersistentListSerializer : KSerializer<PersistentList<String>> {
    private val listSerializer = ListSerializer(String.serializer())
    override fun deserialize(decoder: Decoder): PersistentList<String> {
        return listSerializer.deserialize(decoder).toPersistentList()
    }

    override val descriptor: SerialDescriptor
        get() = SerialDescriptor("PersistentListSerializer", listSerializer.descriptor)

    override fun serialize(encoder: Encoder, value: PersistentList<String>) {
        listSerializer.serialize(encoder, value)
    }

}
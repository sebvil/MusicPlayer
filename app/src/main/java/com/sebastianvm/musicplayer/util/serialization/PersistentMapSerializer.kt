package com.sebastianvm.musicplayer.util.serialization

import com.sebastianvm.musicplayer.util.sort.MediaSortPreferences
import com.sebastianvm.musicplayer.util.sort.SortOptions
import kotlinx.collections.immutable.PersistentMap
import kotlinx.collections.immutable.toPersistentMap
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.serialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.serializer

class PlaylistSortPrefsSerializer :
    KSerializer<PersistentMap<Long, MediaSortPreferences<SortOptions.PlaylistSortOptions>>> {

    private val serializer: KSerializer<Map<Long, MediaSortPreferences<SortOptions.PlaylistSortOptions>>> =
        serializer()

    override val descriptor: SerialDescriptor =
        object :
            SerialDescriptor by serialDescriptor<Map<Long, MediaSortPreferences<SortOptions.PlaylistSortOptions>>>() {}

    override fun deserialize(decoder: Decoder): PersistentMap<Long, MediaSortPreferences<SortOptions.PlaylistSortOptions>> {
        return serializer.deserialize(decoder).toPersistentMap()
    }

    override fun serialize(
        encoder: Encoder,
        value: PersistentMap<Long, MediaSortPreferences<SortOptions.PlaylistSortOptions>>
    ) {
        serializer.serialize(encoder, value.toMap())
    }
}

class GenreSortPrefsSerializer :
    KSerializer<PersistentMap<Long, MediaSortPreferences<SortOptions.TrackListSortOptions>>> {

    private val serializer: KSerializer<Map<Long, MediaSortPreferences<SortOptions.TrackListSortOptions>>> =
        serializer()

    override val descriptor: SerialDescriptor =
        object :
            SerialDescriptor by serialDescriptor<Map<Long, MediaSortPreferences<SortOptions.TrackListSortOptions>>>() {}

    override fun deserialize(decoder: Decoder): PersistentMap<Long, MediaSortPreferences<SortOptions.TrackListSortOptions>> {
        return serializer.deserialize(decoder).toPersistentMap()
    }

    override fun serialize(
        encoder: Encoder,
        value: PersistentMap<Long, MediaSortPreferences<SortOptions.TrackListSortOptions>>
    ) {
        serializer.serialize(encoder, value.toMap())
    }
}

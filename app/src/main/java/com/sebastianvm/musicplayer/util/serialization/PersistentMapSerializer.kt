package com.sebastianvm.musicplayer.util.serialization

import com.sebastianvm.musicplayer.core.model.SortOptions
import com.sebastianvm.musicplayer.util.sort.MediaSortPreferences
import kotlinx.collections.immutable.PersistentMap
import kotlinx.collections.immutable.toPersistentMap
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.serialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.serializer

class PlaylistSortPrefsSerializer :
    KSerializer<PersistentMap<Long, MediaSortPreferences<SortOptions.PlaylistSortOption>>> {

    private val serializer:
        KSerializer<Map<Long, MediaSortPreferences<SortOptions.PlaylistSortOption>>> =
        serializer()

    override val descriptor: SerialDescriptor =
        object :
            SerialDescriptor by serialDescriptor<
                Map<Long, MediaSortPreferences<SortOptions.PlaylistSortOption>>
            >() {}

    override fun deserialize(
        decoder: Decoder
    ): PersistentMap<Long, MediaSortPreferences<SortOptions.PlaylistSortOption>> {
        return serializer.deserialize(decoder).toPersistentMap()
    }

    override fun serialize(
        encoder: Encoder,
        value: PersistentMap<Long, MediaSortPreferences<SortOptions.PlaylistSortOption>>,
    ) {
        serializer.serialize(encoder, value.toMap())
    }
}

class GenreSortPrefsSerializer :
    KSerializer<PersistentMap<Long, MediaSortPreferences<SortOptions.TrackListSortOption>>> {

    private val serializer:
        KSerializer<Map<Long, MediaSortPreferences<SortOptions.TrackListSortOption>>> =
        serializer()

    override val descriptor: SerialDescriptor =
        object :
            SerialDescriptor by serialDescriptor<
                Map<Long, MediaSortPreferences<SortOptions.TrackListSortOption>>
            >() {}

    override fun deserialize(
        decoder: Decoder
    ): PersistentMap<Long, MediaSortPreferences<SortOptions.TrackListSortOption>> {
        return serializer.deserialize(decoder).toPersistentMap()
    }

    override fun serialize(
        encoder: Encoder,
        value: PersistentMap<Long, MediaSortPreferences<SortOptions.TrackListSortOption>>,
    ) {
        serializer.serialize(encoder, value.toMap())
    }
}

package com.sebastianvm.musicplayer.core.model

import kotlinx.serialization.Serializable

sealed interface SortOptions {

    val name: String

    @Serializable sealed interface PlaylistSortOption : SortOptions

    @Serializable sealed interface TrackListSortOption : PlaylistSortOption

    @Serializable sealed interface AlbumListSortOption : SortOptions

    @Serializable
    data object Track : TrackListSortOption {
        override val name: String = "TRACK"
    }

    @Serializable
    data object Album : AlbumListSortOption {
        override val name: String = "ALBUM"
    }

    @Serializable
    data object Artist : TrackListSortOption, AlbumListSortOption {
        override val name: String = "ARTIST"
    }

    @Serializable
    data object Custom : PlaylistSortOption {
        override val name: String = "CUSTOM"
    }

    @Serializable
    data object Year : AlbumListSortOption {
        override val name: String = "YEAR"
    }

    companion object {
        val forTracks: List<TrackListSortOption> = listOf(Track, Artist)
        val forAlbums: List<AlbumListSortOption> = listOf(Album, Artist, Year)
        val forPlaylist: List<PlaylistSortOption> = listOf(Track, Artist, Custom)
    }
}

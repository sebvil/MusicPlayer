package com.sebastianvm.musicplayer.player

enum class MediaGroupType {
    ALL_TRACKS,
    SINGLE_TRACK,
    ARTIST,
    ALBUM,
    GENRE,
    PLAYLIST,
    UNKNOWN,
}

// used for MediaGroups that are TrackLists
enum class TracksListType {
    ALL_TRACKS,
    GENRE,
}

data class MediaGroup(val mediaGroupType: MediaGroupType, val mediaId: Long)

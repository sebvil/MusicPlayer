package com.sebastianvm.musicplayer.ui.components.lists.tracklist

import com.sebastianvm.musicplayer.player.NewTrackListType
import com.sebastianvm.musicplayer.ui.navigation.NavigationArguments
import kotlinx.parcelize.Parcelize

@kotlinx.serialization.Serializable
@Parcelize
data class TrackListComponentArgs(val trackListId: Long, val trackListType: NewTrackListType) :
    NavigationArguments
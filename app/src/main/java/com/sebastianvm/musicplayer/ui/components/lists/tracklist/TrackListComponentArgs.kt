package com.sebastianvm.musicplayer.ui.components.lists.tracklist

import com.sebastianvm.musicplayer.player.TrackListType
import com.sebastianvm.musicplayer.ui.navigation.NavigationArguments
import kotlinx.parcelize.Parcelize

@kotlinx.serialization.Serializable
@Parcelize
data class TrackListComponentArgs(val trackListId: Long, val trackListType: TrackListType) :
    NavigationArguments
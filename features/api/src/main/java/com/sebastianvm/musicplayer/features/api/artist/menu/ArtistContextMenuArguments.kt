package com.sebastianvm.musicplayer.features.api.artist.menu

import com.sebastianvm.musicplayer.core.ui.mvvm.Arguments
import kotlinx.parcelize.Parcelize

@Parcelize data class ArtistContextMenuArguments(val artistId: Long) : Arguments

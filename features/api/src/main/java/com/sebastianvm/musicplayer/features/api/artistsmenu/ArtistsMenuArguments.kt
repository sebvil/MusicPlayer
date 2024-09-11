package com.sebastianvm.musicplayer.features.api.artistsmenu

import com.sebastianvm.musicplayer.core.model.HasArtists
import com.sebastianvm.musicplayer.core.ui.mvvm.Arguments
import kotlinx.parcelize.Parcelize

@Parcelize data class ArtistsMenuArguments(val media: HasArtists) : Arguments

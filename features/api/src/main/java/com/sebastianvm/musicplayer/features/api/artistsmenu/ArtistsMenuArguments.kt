package com.sebastianvm.musicplayer.features.api.artistsmenu

import com.sebastianvm.musicplayer.core.model.HasArtists
import com.sebastianvm.musicplayer.core.ui.mvvm.Arguments

data class ArtistsMenuArguments(val media: HasArtists) : Arguments

package com.sebastianvm.musicplayer.features.api.artist.details

import com.sebastianvm.musicplayer.core.ui.mvvm.Arguments
import kotlinx.parcelize.Parcelize

@Parcelize data class ArtistDetailsArguments(val artistId: Long) : Arguments

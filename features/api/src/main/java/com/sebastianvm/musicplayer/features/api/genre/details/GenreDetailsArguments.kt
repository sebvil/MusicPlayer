package com.sebastianvm.musicplayer.features.api.genre.details

import com.sebastianvm.musicplayer.core.ui.mvvm.Arguments

data class GenreDetailsArguments(
    val genreId: Long,
    val genreName: String,
) : Arguments

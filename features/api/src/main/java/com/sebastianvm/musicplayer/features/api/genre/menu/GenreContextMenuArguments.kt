package com.sebastianvm.musicplayer.features.api.genre.menu

import com.sebastianvm.musicplayer.core.ui.mvvm.Arguments
import kotlinx.parcelize.Parcelize

@Parcelize data class GenreContextMenuArguments(val genreId: Long) : Arguments

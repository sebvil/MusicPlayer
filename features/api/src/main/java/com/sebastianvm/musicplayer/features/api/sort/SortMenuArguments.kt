package com.sebastianvm.musicplayer.features.api.sort

import com.sebastianvm.musicplayer.core.ui.mvvm.Arguments
import kotlinx.parcelize.Parcelize

@Parcelize data class SortMenuArguments(val listType: SortableListType) : Arguments

package com.sebastianvm.musicplayer.ui.library.root

import androidx.annotation.DrawableRes
import androidx.annotation.PluralsRes
import androidx.annotation.StringRes
import com.sebastianvm.commons.util.ListItem

data class LibraryItem(
    val rowId: String,
    @StringRes val rowName: Int,
    @PluralsRes val countString: Int,
    @DrawableRes val icon: Int,
    val count: Long
) : ListItem {
    override val gid = rowId
}
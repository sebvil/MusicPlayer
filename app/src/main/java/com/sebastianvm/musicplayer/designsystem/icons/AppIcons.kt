package com.sebastianvm.musicplayer.designsystem.icons

import androidx.compose.material.icons.automirrored.filled.PlaylistAdd
import androidx.compose.material.icons.automirrored.filled.QueueMusic
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.PlaylistRemove
import com.sebastianvm.musicplayer.R
import com.sebastianvm.musicplayer.ui.util.compose.IconState
import com.sebastianvm.musicplayer.ui.util.compose.ResourceIcon
import com.sebastianvm.musicplayer.ui.util.compose.VectorIcon

object AppIcons {
    val Album: IconState
        get() = ResourceIcon(R.drawable.ic_album)

    val Artist: IconState
        get() = VectorIcon(androidx.compose.material.icons.Icons.Default.Person)

    val Delete: IconState
        get() = VectorIcon(androidx.compose.material.icons.Icons.Default.Delete)

    val Genre: IconState
        get() = ResourceIcon(resId = R.drawable.ic_genre)

    val PlayArrow: IconState
        get() = VectorIcon(androidx.compose.material.icons.Icons.Default.PlayArrow)

    val Playlist: IconState
        get() = VectorIcon(androidx.compose.material.icons.Icons.AutoMirrored.Default.QueueMusic)

    val PlaylistRemove: IconState
        get() = VectorIcon(androidx.compose.material.icons.Icons.Default.PlaylistRemove)

    val QueueAdd: IconState
        get() = VectorIcon(androidx.compose.material.icons.Icons.AutoMirrored.Default.PlaylistAdd)
}

package com.sebastianvm.musicplayer.core.designsystems.icons

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.PlaylistAdd
import androidx.compose.material.icons.automirrored.filled.QueueMusic
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DragIndicator
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.PlaylistRemove
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import com.sebastianvm.musicplayer.core.designsystems.components.IconState
import com.sebastianvm.musicplayer.core.designsystems.components.ResourceIcon
import com.sebastianvm.musicplayer.core.designsystems.components.VectorIcon
import com.sebastianvm.musicplayer.core.resources.RDrawable

object AppIcons {
    val Album: IconState
        get() = ResourceIcon(RDrawable.ic_album)

    val Artist: IconState
        get() = VectorIcon(Icons.Default.Person)

    val Delete: IconState
        get() = VectorIcon(Icons.Default.Delete)

    val Genre: IconState
        get() = ResourceIcon(resId = RDrawable.ic_genre)

    val PlayArrow: IconState
        get() = VectorIcon(Icons.Default.PlayArrow)

    val Pause: IconState
        get() = VectorIcon(Icons.Default.Pause)

    val QueueMusic: IconState
        get() = VectorIcon(Icons.AutoMirrored.Filled.QueueMusic)

    val Circle: IconState
        get() = VectorIcon(Icons.Default.Circle)

    val SkipNext: IconState
        get() = VectorIcon(Icons.Default.SkipNext)

    val SkipPrevious: IconState
        get() = VectorIcon(Icons.Default.SkipPrevious)

    val Playlist: IconState
        get() = VectorIcon(Icons.AutoMirrored.Default.QueueMusic)

    val PlaylistRemove: IconState
        get() = VectorIcon(Icons.Default.PlaylistRemove)

    val QueueAdd: IconState
        get() = VectorIcon(Icons.AutoMirrored.Default.PlaylistAdd)

    val ArrowDownward: IconState
        get() = VectorIcon(Icons.Default.ArrowDownward)

    val ArrowUpward: IconState
        get() = VectorIcon(Icons.Default.ArrowUpward)

    val DragIndicator: IconState
        get() = VectorIcon(Icons.Default.DragIndicator)
}

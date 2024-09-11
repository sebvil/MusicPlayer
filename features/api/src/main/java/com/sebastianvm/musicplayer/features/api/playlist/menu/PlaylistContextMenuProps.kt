package com.sebastianvm.musicplayer.features.api.playlist.menu

import com.sebastianvm.musicplayer.core.ui.mvvm.Props

data class PlaylistContextMenuProps(val deletePlaylist: () -> Unit) : Props

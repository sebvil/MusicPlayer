package com.sebastianvm.musicplayer.features.api

import com.sebastianvm.musicplayer.features.api.album.details.AlbumDetailsFeature
import com.sebastianvm.musicplayer.features.api.album.list.AlbumListFeature
import com.sebastianvm.musicplayer.features.api.album.menu.AlbumContextMenuFeature
import com.sebastianvm.musicplayer.features.api.sort.SortMenuFeature
import com.sebastianvm.musicplayer.features.api.track.menu.TrackContextMenuFeature

interface Features {
    val albumDetailsFeature: AlbumDetailsFeature
    val albumListFeature: AlbumListFeature
    val albumContextMenuFeature: AlbumContextMenuFeature

    val sortMenuFeature: SortMenuFeature

    val trackContextMenuFeature: TrackContextMenuFeature
}

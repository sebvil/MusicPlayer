package com.sebastianvm.musicplayer.services.features

import com.sebastianvm.musicplayer.services.features.album.details.AlbumDetailsFeature
import com.sebastianvm.musicplayer.services.features.album.list.AlbumListFeature
import com.sebastianvm.musicplayer.services.features.album.menu.AlbumContextMenuFeature
import com.sebastianvm.musicplayer.services.features.sort.SortMenuFeature
import com.sebastianvm.musicplayer.services.features.track.menu.TrackContextMenuFeature

interface Features {
    val albumDetailsFeature: AlbumDetailsFeature
    val albumListFeature: AlbumListFeature
    val albumContextMenuFeature: AlbumContextMenuFeature

    val sortMenuFeature: SortMenuFeature

    val trackContextMenuFeature: TrackContextMenuFeature
}

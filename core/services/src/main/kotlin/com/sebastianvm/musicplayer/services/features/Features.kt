package com.sebastianvm.musicplayer.services.features

import com.sebastianvm.musicplayer.services.features.album.details.AlbumDetailsFeature
import com.sebastianvm.musicplayer.services.features.track.menu.TrackContextMenuFeature

interface Features {
    val trackContextMenuFeature: TrackContextMenuFeature
    val albumDetailsFeature: AlbumDetailsFeature
}

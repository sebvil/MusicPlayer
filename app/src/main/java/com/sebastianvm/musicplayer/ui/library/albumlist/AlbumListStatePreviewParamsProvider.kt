package com.sebastianvm.musicplayer.ui.library.albumlist

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.sebastianvm.musicplayer.database.entities.Album
import com.sebastianvm.musicplayer.ui.components.lists.toModelListItemState
import com.sebastianvm.musicplayer.ui.util.compose.PreviewUtil
import kotlin.random.Random


class AlbumListStatePreviewParamsProvider : PreviewParameterProvider<AlbumListState> {
    override val values: Sequence<AlbumListState>
        get() = sequenceOf(AlbumListState(albumList = (1..10).map {
            Album(
                id = it.toLong(),
                albumName = PreviewUtil.randomString(),
                year = Random.nextLong(1000L, 9999L).let { n -> if (n % 3L == 0L) n else 0L },
                artists = PreviewUtil.randomString(),
                imageUri = ""
            ).toModelListItemState()
        }))
}
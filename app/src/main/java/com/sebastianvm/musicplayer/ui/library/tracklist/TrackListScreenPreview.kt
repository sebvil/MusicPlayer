package com.sebastianvm.musicplayer.ui.library.tracklist

import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.sebastianvm.musicplayer.R
import com.sebastianvm.musicplayer.database.entities.Track
import com.sebastianvm.musicplayer.player.TrackListType
import com.sebastianvm.musicplayer.repository.playback.PlaybackResult
import com.sebastianvm.musicplayer.ui.components.MediaArtImageStatePreviewParamsProvider
import com.sebastianvm.musicplayer.ui.components.lists.ModelListItemState
import com.sebastianvm.musicplayer.ui.components.lists.toModelListItemState
import com.sebastianvm.musicplayer.ui.util.compose.PreviewUtil
import com.sebastianvm.musicplayer.ui.util.compose.ScreenPreview
import com.sebastianvm.musicplayer.ui.util.mvvm.DefaultScreenDelegateProvider
import kotlin.random.Random

class TrackListStatePreviewParamProvider : PreviewParameterProvider<TrackListState> {
    private val trackList: List<ModelListItemState>
        get() = (1..10).map {
            Track(
                id = it.toLong(),
                trackName = PreviewUtil.randomString(),
                trackNumber = Random.nextLong(0, 100),
                artists = PreviewUtil.randomString(),
                albumName = PreviewUtil.randomString(),
                trackDurationMs = Random.nextLong(0, 180000),
                albumId = Random.nextLong(0, 1000),
                path = ""
            ).toModelListItemState()
        }
    override val values: Sequence<TrackListState>
        get() = sequenceOf(
            TrackListState(
                trackList = trackList,
                trackListName = null,
                trackListId = 0,
                trackListType = TrackListType.ALL_TRACKS,
                playbackResult = null,
                headerImage = null
            ),
            TrackListState(
                trackList = trackList,
                trackListName = PreviewUtil.randomString(),
                trackListId = 0,
                trackListType = TrackListType.GENRE,
                playbackResult = null,
                headerImage = null
            ),
            TrackListState(
                trackList = trackList,
                trackListName = PreviewUtil.randomString(),
                trackListId = 0,
                trackListType = TrackListType.PLAYLIST,
                playbackResult = null,
                headerImage = null
            ),
            TrackListState(
                trackList = trackList,
                trackListName = PreviewUtil.randomString(),
                trackListId = 0,
                trackListType = TrackListType.ALBUM,
                playbackResult = null,
                headerImage = MediaArtImageStatePreviewParamsProvider().values.toList().random()
            ),
            TrackListState(
                trackList = trackList,
                trackListName = PreviewUtil.randomString(),
                trackListId = 0,
                trackListType = TrackListType.ALBUM,
                playbackResult = PlaybackResult.Loading,
                headerImage = MediaArtImageStatePreviewParamsProvider().values.toList().random()
            ),
            TrackListState(
                trackList = trackList,
                trackListName = PreviewUtil.randomString(),
                trackListId = 0,
                trackListType = TrackListType.ALBUM,
                playbackResult = PlaybackResult.Error(R.string.playback_error),
                headerImage = MediaArtImageStatePreviewParamsProvider().values.toList().random()
            )
        )
}

@ScreenPreview
@Composable
private fun TrackListScreenPreview(@PreviewParameter(TrackListStatePreviewParamProvider::class) state: TrackListState) {
    ScreenPreview {
        TrackListScreen(
            state = state,
            screenDelegate = DefaultScreenDelegateProvider.getDefaultInstance(),
            listState = rememberLazyListState()
        )
    }
}
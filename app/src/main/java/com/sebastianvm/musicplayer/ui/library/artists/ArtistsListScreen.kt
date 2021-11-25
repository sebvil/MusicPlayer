package com.sebastianvm.musicplayer.ui.library.artists

import android.content.res.Configuration
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sebastianvm.commons.util.DisplayableString
import com.sebastianvm.musicplayer.R
import com.sebastianvm.musicplayer.ui.components.LibraryTitle
import com.sebastianvm.musicplayer.ui.components.ListWithHeader
import com.sebastianvm.musicplayer.ui.components.ListWithHeaderState
import com.sebastianvm.musicplayer.ui.util.compose.AppDimensions
import com.sebastianvm.musicplayer.ui.util.compose.Screen
import com.sebastianvm.musicplayer.ui.util.compose.ScreenPreview

@Composable
fun ArtistsListScreen(
    screenViewModel: ArtistsListViewModel = viewModel(),
    navigateToArtist: (String, String) -> Unit
) {

    Screen(screenViewModel = screenViewModel, eventHandler = { event ->
        when (event) {
            is ArtistsListUiEvent.NavigateToArtist -> {
                navigateToArtist(event.artistGid, event.artistName)
            }
        }

    }) { state ->
        ArtistsListLayout(state = state, delegate = object : ArtistsListScreenDelegate {
            override fun onArtistRowClicked(artistGid: String, artistName: String) {
                screenViewModel.handle(
                    ArtistsListUserAction.ArtistClicked(
                        artistGid = artistGid,
                        artistName = artistName
                    )
                )
            }
        })
    }
}

interface ArtistsListScreenDelegate {
    fun onArtistRowClicked(artistGid: String, artistName: String)
}

@Preview(showSystemUi = true)
@Preview(showSystemUi = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun ArtistsListScreenPreview(@PreviewParameter(ArtistsListStatePreviewParameterProvider::class) state: ArtistsListState) {
    ScreenPreview {
        ArtistsListLayout(state = state, delegate = object : ArtistsListScreenDelegate {
            override fun onArtistRowClicked(artistGid: String, artistName: String) = Unit
        })
    }
}

@Composable
fun ArtistsListLayout(
    state: ArtistsListState,
    delegate: ArtistsListScreenDelegate
) {
    val listState = ListWithHeaderState(
        DisplayableString.ResourceValue(R.string.artists),
        state.artistsList,
        { header -> LibraryTitle(title = header) },
        { item ->
            ArtistRow(artistItem = item, modifier = Modifier
                .clickable {
                    delegate.onArtistRowClicked(item.artistGid, item.artistName)
                }
                .fillMaxWidth()
                .padding(
                    vertical = AppDimensions.spacing.mediumSmall,
                    horizontal = AppDimensions.spacing.large
                )
            )
        }
    )
    ListWithHeader(state = listState)
}

@Preview(showBackground = true)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun ArtistRowPreview(@PreviewParameter(ArtistListItemProvider::class) artistItem: ArtistsListItem) {
    ArtistRow(artistItem = artistItem)
}


@Composable
fun ArtistRow(
    artistItem: ArtistsListItem,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier) {
        Row(
            modifier = Modifier.height(IntrinsicSize.Min),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                color = MaterialTheme.colorScheme.inverseSurface,
                modifier = Modifier.clip(CircleShape)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_artist),
                    contentDescription = stringResource(id = R.string.placeholder_artist_image),
                    modifier = Modifier
                        .fillMaxHeight()
                        .padding(AppDimensions.spacing.mediumSmall)
                        .aspectRatio(1f, matchHeightConstraintsFirst = true)
                )
            }
            Text(
                text = artistItem.artistName,
                style = MaterialTheme.typography.titleLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .padding(vertical = AppDimensions.spacing.mediumSmall)
                    .padding(start = AppDimensions.spacing.medium)
            )
        }
    }
}
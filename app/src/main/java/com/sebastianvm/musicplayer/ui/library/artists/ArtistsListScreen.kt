package com.sebastianvm.musicplayer.ui.library.artists

import android.content.res.Configuration
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.sebastianvm.musicplayer.ui.components.lists.ListItemDelegate
import com.sebastianvm.musicplayer.ui.components.lists.SingleLineListItem
import com.sebastianvm.musicplayer.ui.components.lists.SupportingImageType
import com.sebastianvm.musicplayer.ui.util.compose.AppDimensions
import com.sebastianvm.musicplayer.ui.util.compose.Screen
import com.sebastianvm.musicplayer.ui.util.compose.ScreenPreview

@Composable
fun ArtistsListScreen(
    screenViewModel: ArtistsListViewModel = viewModel(),
    bottomNavBar: @Composable () -> Unit,
    navigateToArtist: (String) -> Unit
) {

    Screen(
        screenViewModel = screenViewModel,
        eventHandler = { event ->
            when (event) {
                is ArtistsListUiEvent.NavigateToArtist -> {
                    navigateToArtist(event.artistGid)
                }
            }

        },
        bottomNavBar = bottomNavBar
    ) { state ->
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
            SingleLineListItem(
                supportingImage = { modifier ->
                    Surface(
                        color = MaterialTheme.colorScheme.inverseSurface,
                        modifier = modifier.clip(CircleShape)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_artist),
                            contentDescription = stringResource(id = R.string.placeholder_artist_image),
                            modifier = Modifier
                                .fillMaxHeight()
                                .padding(AppDimensions.spacing.xSmall)
                                .aspectRatio(1f, matchHeightConstraintsFirst = true)
                        )
                    }
                },
                supportingImageType = SupportingImageType.AVATAR,
                delegate = object : ListItemDelegate {
                    override fun onItemClicked() {
                        delegate.onArtistRowClicked(item.artistGid, item.artistName)
                    }
                }
            ) {
                Text(
                    text = item.artistName,
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    )
    ListWithHeader(state = listState)
}



package com.sebastianvm.musicplayer.ui.components

import android.content.ContentUris
import android.content.Context
import android.content.res.AssetFileDescriptor
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.graphics.ImageDecoder.ImageInfo
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.paddingFromBaseline
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import coil.annotation.ExperimentalCoilApi
import coil.bitmap.BitmapPool
import coil.decode.DataSource
import coil.decode.Options
import coil.fetch.DrawableResult
import coil.fetch.FetchResult
import coil.fetch.Fetcher
import coil.request.CachePolicy
import coil.request.ImageRequest
import coil.size.Size
import com.sebastianvm.commons.R
import com.sebastianvm.commons.util.DisplayableString
import com.sebastianvm.commons.util.MediaArt
import com.sebastianvm.musicplayer.database.entities.AlbumWithArtists
import com.sebastianvm.musicplayer.ui.components.lists.DoubleLineListItem
import com.sebastianvm.musicplayer.ui.components.lists.SupportingImageType
import com.sebastianvm.musicplayer.ui.util.compose.AppDimensions
import com.sebastianvm.musicplayer.ui.util.compose.ThemedPreview
import com.sebastianvm.musicplayer.util.ArtLoader
import com.skydoves.landscapist.rememberDrawablePainter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.FileNotFoundException


data class AlbumRowState(
    val albumId: String,
    val albumName: String,
    val image: MediaArt,
    val year: Long,
    val artists: String,
)

@Preview(showBackground = true)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun AlbumRowPreview(@PreviewParameter(AlbumRowStateProvider::class) state: AlbumRowState) {
    ThemedPreview {
        AlbumRow(state = state) {}
    }
}


class ThumbnailFetcher(private val context: Context) : Fetcher<Uri> {
    @RequiresApi(Build.VERSION_CODES.Q)
    override suspend fun fetch(
        pool: BitmapPool,
        data: Uri,
        size: Size,
        options: Options
    ): FetchResult {
        val bitmap: Bitmap?
        Log.i("COIL", "Loading image")
        withContext(Dispatchers.IO) {
            bitmap = try {
                context.contentResolver.loadThumbnail(
                    data,
                    android.util.Size(500, 500),
                    null
                )
            } catch (e: FileNotFoundException) {
                null
            }
            checkNotNull(bitmap) { "Could not load bitmap" }
        }

        return DrawableResult(
            drawable = BitmapDrawable(context.resources, bitmap),
            isSampled = true,
            dataSource = DataSource.DISK
        )

    }

    override fun key(data: Uri): String {
        return data.hashCode().toString()
    }

}

@Composable
@ReadOnlyComposable
fun getBitmap(uri: Uri): Bitmap? {
    val context = LocalContext.current
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        ImageDecoder.decodeBitmap(ImageDecoder.createSource {
            val afd: AssetFileDescriptor = context.contentResolver.openTypedAssetFile(
                uri, "image/*", Bundle(),
                null
            ) ?: return@createSource null
            val extras = afd.extras
            afd
        }) { _: ImageDecoder, _: ImageInfo, _: ImageDecoder.Source? ->

        }
    } else {
        null
    }
}


//@RequiresApi(Build.VERSION_CODES.Q)
//@OptIn(ExperimentalCoilApi::class)
@OptIn(ExperimentalCoilApi::class)
@Composable
fun AlbumRow(
    state: AlbumRowState,
    modifier: Modifier = Modifier,
    onOverflowMenuIconClicked: () -> Unit
) {


    val uri = ContentUris.withAppendedId(
        MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI, state.albumId.toLong()
    )
//        val image = getBitmap(uri = uri)
//    val painter = rememberImagePainter(
//        data = uri,
//        builder = {
//            crossfade(false)
//            memoryCachePolicy(CachePolicy.ENABLED)
//            fallback(R.drawable.ic_album)
//            placeholder(R.drawable.ic_album)
//            error(R.drawable.ic_album)
//        }
//    )
    with(state) {
        DoubleLineListItem(
            modifier = modifier,
            supportingImage = { thisM ->
                androidx.compose.material3.Surface(
                    color = MaterialTheme.colorScheme.inverseSurface,
                    modifier = thisM
                ) {
                    com.skydoves.landscapist.coil.CoilImage(
                        imageRequest = ImageRequest.Builder(context = LocalContext.current)
                            .data(uri).apply {
                                crossfade(false)
                                memoryCachePolicy(CachePolicy.ENABLED)
                                fallback(R.drawable.ic_album)
                                placeholder(R.drawable.ic_album)
                                error(R.drawable.ic_album)
                            }.build(),
                        modifier = thisM,
                        contentScale = ContentScale.Fit,
                        alignment = Alignment.Center,
                        loading = {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_album),
                                contentDescription = "",
                            )
                        },
                        success = { successState ->
                            Image(
                                painter = rememberDrawablePainter(drawable = successState.drawable),
                                contentDescription = image.contentDescription.getString(),
                            )
                        },
                        failure = { error ->
                            Log.i("ERROR", "$error.")
                            Icon(imageVector = Icons.Default.Close, contentDescription = "")
                        }

                    )
                }
            },
            supportingImageType = SupportingImageType.LARGE,
            afterListContent = {
                IconButton(
                    onClick = onOverflowMenuIconClicked,
                    modifier = Modifier.padding(end = AppDimensions.spacing.xSmall)
                ) {
                    Icon(
                        painter = painterResource(id = com.sebastianvm.musicplayer.R.drawable.ic_overflow),
                        contentDescription = stringResource(com.sebastianvm.musicplayer.R.string.more)
                    )
                }
            },
            secondaryText = {
                Row {
                    if (year != 0L) {
                        Text(
                            text = year.toString(),
                            style = MaterialTheme.typography.bodySmall,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier
                                .paddingFromBaseline(top = AppDimensions.spacing.mediumLarge)
                                .padding(end = AppDimensions.spacing.small)
                        )
                    }
                    Text(
                        text = artists,
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.paddingFromBaseline(top = AppDimensions.spacing.mediumLarge)
                    )
                }
            }) {
            Text(
                text = albumName,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.paddingFromBaseline(top = AppDimensions.spacing.xxLarge)
            )
        }
    }
}


fun AlbumWithArtists.toAlbumRowState(): AlbumRowState {
    return AlbumRowState(
        albumId = album.albumId,
        albumName = album.albumName,
        image = ArtLoader.getAlbumArt(
            albumId = album.albumId,
            albumName = album.albumName
        ),
        year = album.year,
        artists = artists.joinToString(", ") { it.artistName }
    )

}

class AlbumRowStateProvider : PreviewParameterProvider<AlbumRowState> {
    override val values =
        sequenceOf(
            AlbumRowState(
                albumId = "1",
                albumName = "Ahora",
                image = MediaArt(
                    uris = listOf(),
                    contentDescription = DisplayableString.StringValue(""),
                    backupResource = R.drawable.ic_album,
                    backupContentDescription = DisplayableString.StringValue("Album art placeholder")
                ),
                year = 2017,
                artists = "Melendi"

            ),
            AlbumRowState(
                albumId = "2",
                albumName = "VIVES",
                image = MediaArt(
                    uris = listOf(),
                    contentDescription = DisplayableString.StringValue(""),
                    backupResource = R.drawable.ic_album,
                    backupContentDescription = DisplayableString.StringValue("Album art placeholder")
                ),
                year = 2017,
                artists = "Carlos Vives"
            ),
        )
}

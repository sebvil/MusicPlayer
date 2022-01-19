package com.sebastianvm.musicplayer.ui.util.images

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import coil.bitmap.BitmapPool
import coil.decode.DataSource
import coil.decode.Options
import coil.fetch.DrawableResult
import coil.fetch.FetchResult
import coil.fetch.Fetcher
import coil.size.Size
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.FileNotFoundException

class ThumbnailFetcher(private val context: Context) : Fetcher<Uri> {
    @Suppress("BlockingMethodInNonBlockingContext")
    @RequiresApi(Build.VERSION_CODES.Q)
    override suspend fun fetch(
        pool: BitmapPool,
        data: Uri,
        size: Size,
        options: Options
    ): FetchResult {
        val bitmap: Bitmap?
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

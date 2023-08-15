package com.sebastianvm.musicplayer

import android.content.ContentProvider
import android.content.ContentResolver
import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.content.res.Resources
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.VectorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.ParcelFileDescriptor
import android.provider.MediaStore
import android.util.Size
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat
import java.io.FileOutputStream

class ArtworkProvider : ContentProvider() {

    override fun onCreate(): Boolean {
        return true
    }

    override fun query(
        p0: Uri,
        p1: Array<out String>?,
        p2: String?,
        p3: Array<out String>?,
        p4: String?
    ): Cursor? = null

    override fun getType(p0: Uri): String? = null

    override fun insert(p0: Uri, p1: ContentValues?): Uri? = null

    override fun delete(p0: Uri, p1: String?, p2: Array<out String>?): Int = 0

    override fun update(p0: Uri, p1: ContentValues?, p2: String?, p3: Array<out String>?): Int = 0

    private fun getPFDFromBitmap(bitmap: Bitmap?): ParcelFileDescriptor {
        return super.openPipeHelper(
            Uri.EMPTY,
            "image/*",
            null,
            bitmap
        ) { pfd: ParcelFileDescriptor, _: Uri, _: String, _: Bundle?, b: Bitmap? ->
            /* Compression is performed on an AsyncTask thread within openPipeHelper() */
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                b?.compress(
                    Bitmap.CompressFormat.WEBP_LOSSLESS,
                    100,
                    FileOutputStream(pfd.fileDescriptor)
                )
            } else {
                @Suppress("DEPRECATION")
                b?.compress(
                    Bitmap.CompressFormat.WEBP,
                    100,
                    FileOutputStream(pfd.fileDescriptor)
                )
            }
        }
    }

    override fun openFile(uri: Uri, mode: String): ParcelFileDescriptor? {
        val nonNullContext = context ?: return null
        val backUpResource = when (uri.pathSegments[0]) {
            TRACK_PATH -> R.drawable.ic_genre_grey
            ALBUM_PATH -> R.drawable.ic_album_grey
            else -> 0
        }
        val path = getUri(uri)
        val bitmap = try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                nonNullContext.contentResolver.loadThumbnail(path, Size(500, 500), null)
            } else {
                null
            }
        } catch (e: Exception) {
            null
        } ?: nonNullContext.getBitmapFromDrawable(backUpResource)
        return getPFDFromBitmap(bitmap)
    }

    private fun getUri(uri: Uri): Uri {
        val id = ContentUris.parseId(uri)
        return ContentUris.withAppendedId(MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI, id)
    }

    private fun Context.getBitmapFromDrawable(
        @DrawableRes drawableId: Int,
        width: Int = -1,
        height: Int = -1
    ): Bitmap? {
        val drawable: Drawable = try {
            ContextCompat.getDrawable(this, drawableId) ?: return null
        } catch (e: Resources.NotFoundException) {
            VectorDrawableCompat.create(this.resources, drawableId, this.theme)!!
        }

        return when (drawable) {
            is BitmapDrawable -> drawable.bitmap
            is VectorDrawableCompat, is VectorDrawable -> {
                val bitmap = if (width > 0 && height > 0) {
                    Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
                } else {
                    Bitmap.createBitmap(
                        drawable.intrinsicWidth,
                        drawable.intrinsicHeight,
                        Bitmap.Config.ARGB_8888
                    )
                }
                val canvas = Canvas(bitmap)
                drawable.setBounds(0, 0, canvas.width, canvas.height)
                drawable.draw(canvas)
                bitmap
            }
            else -> BitmapFactory.decodeResource(this.resources, drawableId)
        }
    }

    companion object {
        private const val AUTHORITY = "com.sebastianvm.musicplayer.provider"
        private const val TRACK_PATH = "TRACK"
        private const val ALBUM_PATH = "ALBUM"

        fun getUriForTrack(albumId: Long): Uri {
            return ContentUris.withAppendedId(
                Uri.Builder().scheme(ContentResolver.SCHEME_CONTENT).authority(AUTHORITY)
                    .appendPath(
                        TRACK_PATH
                    ).build(),
                albumId
            )
        }

        fun getUriForAlbum(albumId: Long): Uri {
            return ContentUris.withAppendedId(
                Uri.Builder().scheme(ContentResolver.SCHEME_CONTENT).authority(AUTHORITY)
                    .appendPath(
                        ALBUM_PATH
                    ).build(),
                albumId
            )
        }
    }
}

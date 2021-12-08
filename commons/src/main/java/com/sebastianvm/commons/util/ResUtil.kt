package com.sebastianvm.commons.util

import android.content.Context
import android.net.Uri
import android.os.Build
import android.util.Size
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import java.io.FileNotFoundException


class ResUtil {

    companion object {
        @JvmStatic
        fun getString(
            context: Context,
            @StringRes stringResource: Int,
            vararg formatArgs: Any
        ): String {
            return context.getString(stringResource, *formatArgs)
        }

        @JvmStatic
        fun getQuantityString(
            context: Context,
            id: Int,
            quantity: Int,
            vararg formatArgs: Any
        ): String {
            return context.resources.getQuantityString(id, quantity, *formatArgs)
        }


    }

}

sealed class DisplayableString {
    data class ResourceValue(
        @StringRes val value: Int,
        val formatArgs: Array<out Any> = arrayOf()
    ) : DisplayableString() {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as ResourceValue

            if (value != other.value) return false
            if (!formatArgs.contentEquals(other.formatArgs)) return false

            return true
        }

        override fun hashCode(): Int {
            var result = value
            result = 31 * result + formatArgs.contentHashCode()
            return result
        }
    }

    data class StringValue(val value: String) : DisplayableString()

    @Composable
    fun getString(): String {
        return when (this) {
            is ResourceValue -> stringResource(id = this.value, formatArgs = formatArgs)
            is StringValue -> this.value
        }
    }
}

data class MediaArt(
    val uris: List<Uri>,
    val contentDescription: DisplayableString,
    @DrawableRes val backupResource: Int,
    val backupContentDescription: DisplayableString
) {
    @Composable
    fun getBitmap(): ImageBitmap? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            var bitmap: ImageBitmap? = null
            val context = LocalContext.current
            for (uri in this.uris) {
                try {
                    bitmap = context.contentResolver.loadThumbnail(
                        uri,
                        Size(500, 500),
                        null
                    ).asImageBitmap()
                } catch (e: FileNotFoundException) {
                }
                if (bitmap != null) {
                    break
                }
            }
            bitmap
        } else {
            null
        }
    }
}

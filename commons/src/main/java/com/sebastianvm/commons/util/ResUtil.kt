package com.sebastianvm.commons.util

import android.content.Context
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource


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

        @Composable
        @ReadOnlyComposable
        fun getQuantityString(
            id: Int,
            quantity: Int,
            vararg formatArgs: Any
        ): String {
            return LocalContext.current.resources.getQuantityString(id, quantity, *formatArgs)
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

    companion object {
        fun String.toDisplayableString() = StringValue(this)
        fun Int.toDisplayableString() = ResourceValue(this)
    }
}

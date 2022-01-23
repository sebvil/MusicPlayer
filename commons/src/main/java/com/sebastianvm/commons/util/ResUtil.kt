package com.sebastianvm.commons.util

import android.content.Context
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.platform.LocalContext


class ResUtil {

    companion object {
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

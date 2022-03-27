package com.sebastianvm.commons.util

import android.content.Context
import androidx.annotation.StringRes


class ResUtil {

    companion object {
        fun getString(
            context: Context,
            @StringRes stringResource: Int,
            vararg formatArgs: Any
        ): String {
            return context.getString(stringResource, *formatArgs)
        }

    }
}

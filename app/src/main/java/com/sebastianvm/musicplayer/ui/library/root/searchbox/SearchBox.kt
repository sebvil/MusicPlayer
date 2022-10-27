package com.sebastianvm.musicplayer.ui.library.root.searchbox

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.sebastianvm.musicplayer.R
import com.sebastianvm.musicplayer.ui.util.compose.AppDimensions

@Composable
fun SearchBox(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(AppDimensions.spacing.small)
            )
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_search),
            contentDescription = stringResource(id = R.string.search),
            modifier = Modifier.padding(all = AppDimensions.spacing.small),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

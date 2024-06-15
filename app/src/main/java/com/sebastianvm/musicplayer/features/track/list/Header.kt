package com.sebastianvm.musicplayer.features.track.list

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.zIndex
import com.sebastianvm.musicplayer.designsystem.components.Text
import com.sebastianvm.musicplayer.util.resources.RString

@Composable
fun TopBar(
    title: String,
    modifier: Modifier = Modifier,
    alpha: Float = 1f,
    onSizeChange: (Int) -> Unit = {},
    onBackButtonClick: () -> Unit = {},
) {
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .zIndex(1f)
                .background(MaterialTheme.colorScheme.background.copy(alpha = alpha))
                .onSizeChanged { onSizeChange(it.height) }
                .padding(top = WindowInsets.systemBars.asPaddingValues().calculateTopPadding()),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(onClick = onBackButtonClick) {
            Icon(
                imageVector = Icons.AutoMirrored.Default.ArrowBack,
                contentDescription = stringResource(id = RString.back),
            )
        }
        Text(
            text = title,
            modifier = Modifier.alpha(alpha),
            style = MaterialTheme.typography.headlineSmall,
        )
    }
}

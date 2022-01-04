package com.sebastianvm.musicplayer.ui.components.chip

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sebastianvm.musicplayer.R
import com.sebastianvm.musicplayer.ui.util.compose.AppDimensions
import com.sebastianvm.musicplayer.ui.util.compose.ThemedPreview


@Composable
fun Chip(
    text: String,
    modifier: Modifier = Modifier,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
) {
    Row(
        modifier = modifier
            .height(32.dp)
            .padding(horizontal = AppDimensions.spacing.small),
        verticalAlignment = Alignment.CenterVertically
    ) {
        leadingIcon?.also {
            Box(modifier = Modifier.size(18.dp), contentAlignment = Alignment.Center) {
                leadingIcon()
            }
        }
        Text(text = text, modifier = Modifier.padding(horizontal = AppDimensions.spacing.small))
        trailingIcon?.also {
            Box(modifier = Modifier.size(18.dp), contentAlignment = Alignment.Center) {
                trailingIcon()
            }
        }
    }
}

@Composable
fun OutlinedChip(
    text: String,
    modifier: Modifier = Modifier,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
) {
    Chip(
        text = text,
        modifier = modifier.border(
            width = 1.dp,
            color = MaterialTheme.colorScheme.outline,
            shape = RoundedCornerShape(8.dp)
        ),
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon
    )
}

@Composable
fun FilterChip(selected: Boolean, text: String, modifier: Modifier = Modifier) {
    if (selected) {
        Chip(
            text = text,
            modifier = modifier.background(
                color = MaterialTheme.colorScheme.secondaryContainer,
                shape = RoundedCornerShape(8.dp)
            ),
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = stringResource(R.string.check_mark)
                )
            }
        )
    } else {
        OutlinedChip(text = text, modifier = modifier)
    }

}

@Preview
@Composable
fun ChipsPreview() {
    ThemedPreview {
        Column {
            Box(modifier = Modifier.padding(AppDimensions.spacing.medium)) {
                OutlinedChip(text = "Test chip")
            }

            Box(modifier = Modifier.padding(AppDimensions.spacing.medium)) {
                OutlinedChip(text = "Test chip",
                    leadingIcon = {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_genre),
                            contentDescription = ""
                        )
                    })
            }

            Box(modifier = Modifier.padding(AppDimensions.spacing.medium)) {
                OutlinedChip(text = "Test chip",
                    trailingIcon = {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_close),
                            contentDescription = ""
                        )
                    })
            }

            Box(modifier = Modifier.padding(AppDimensions.spacing.medium)) {
                OutlinedChip(text = "Test chip",
                    leadingIcon = {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_genre),
                            contentDescription = ""
                        )
                    }, trailingIcon = {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_close),
                            contentDescription = ""
                        )
                    })
            }

            Box(modifier = Modifier.padding(AppDimensions.spacing.medium)) {
                Text(text = "Filter chips")
            }

            Box(modifier = Modifier.padding(AppDimensions.spacing.medium)) {
                FilterChip(text = "filter chip", selected = true)
            }

            Box(modifier = Modifier.padding(AppDimensions.spacing.medium)) {
                FilterChip(text = "filter chip", selected = false)
            }
        }
    }
}
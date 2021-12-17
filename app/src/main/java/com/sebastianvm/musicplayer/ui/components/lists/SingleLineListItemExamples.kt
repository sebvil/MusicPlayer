package com.sebastianvm.musicplayer.ui.components.lists

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Divider
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.sebastianvm.commons.util.DisplayableString
import com.sebastianvm.musicplayer.R
import com.sebastianvm.musicplayer.ui.util.compose.ThemedPreview

@Preview
@Composable
fun SingleLineListItemExamples() {
    ThemedPreview {
        Column {
            SingleLineListItem(text = DisplayableString.StringValue("Simple Item"))
            Divider()
            SingleLineListItem(
                text = DisplayableString.StringValue("Item with Icon and metadata"),
                supportingImage = { modifier ->
                    Icon(
                        painter = painterResource(id = R.drawable.ic_album),
                        contentDescription = "",
                        modifier = modifier
                    )
                },
                supportingImageType = SupportingImageType.ICON,
                metadata = DisplayableString.StringValue("3")
            )
            Divider()
            SingleLineListItem(
                text = DisplayableString.StringValue("Item with Icon"),
                supportingImage = { modifier ->
                    Icon(
                        painter = painterResource(id = R.drawable.ic_album),
                        contentDescription = "",
                        modifier = modifier
                    )
                },
                supportingImageType = SupportingImageType.ICON
            )
            Divider()
            SingleLineListItem(
                text = DisplayableString.StringValue("Item with Avatar"),
                supportingImage = { modifier ->
                    Icon(
                        painter = painterResource(id = R.drawable.ic_album),
                        contentDescription = "",
                        modifier = modifier
                    )
                },
                supportingImageType = SupportingImageType.AVATAR
            )
            Divider()
            SingleLineListItem(
                text = DisplayableString.StringValue("Item with Large Image"),
                supportingImage = { modifier ->
                    Icon(
                        painter = painterResource(id = R.drawable.ic_album),
                        contentDescription = "",
                        modifier = modifier
                    )
                },
                supportingImageType = SupportingImageType.LARGE
            )
            Divider()
            SingleLineListItem(
                text = DisplayableString.StringValue("Item with Large Image and metadata"),
                supportingImage = { modifier ->
                    Icon(
                        painter = painterResource(id = R.drawable.ic_album),
                        contentDescription = "",
                        modifier = modifier
                    )
                },
                supportingImageType = SupportingImageType.LARGE,
                metadata = DisplayableString.StringValue("4")
            )
        }
    }
}
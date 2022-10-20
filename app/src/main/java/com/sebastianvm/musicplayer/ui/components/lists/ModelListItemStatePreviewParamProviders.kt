package com.sebastianvm.musicplayer.ui.components.lists

import androidx.compose.ui.tooling.preview.PreviewParameterProvider


class BasicSingleLineNoImageModelListItemStatePreviewParameterProvider() :
    PreviewParameterProvider<ModelListItemState> {
    override val values: Sequence<ModelListItemState>
        get() = sequenceOf(
            "Lorem ipsum dolor sit amet",
            "consectetur adipiscing elit",
            "sed do eiusmod tempor incididunt ut labore",
            "et dolore magna aliqua",
            "Ut enim ad minim veniam",
            "quis nostrud exercitation ullamco laboris nisi ut",
            "aliquip ex ea commodo consequat",
            "Duis aute irure dolor in reprehenderit in voluptate velit esse",
            "cillum dolore eu fugiat nulla pariatur",
            "Excepteur sint occaecat cupidatat non proident",
            "culpa qui officia deserunt mollit anim id est laborum",
            " sunt in"
        ).mapIndexed { index, s -> ModelListItemState.Basic(id = index.toLong(), headlineText = s) }
}
package com.sebastianvm.musicplayer.features.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.sebastianvm.musicplayer.ui.util.mvvm.Arguments

interface Screen<Args : Arguments> {

    val arguments: Args

    @Composable
    fun Content(modifier: Modifier)
}

@Composable
fun <Args : Arguments> Screen<Args>.Content() = Content(modifier = Modifier)

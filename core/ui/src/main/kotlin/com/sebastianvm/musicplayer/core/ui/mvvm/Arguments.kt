package com.sebastianvm.musicplayer.core.ui.mvvm

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

interface Arguments : Parcelable

@Parcelize data object NoArguments : Arguments

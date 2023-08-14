package com.sebastianvm.musicplayer.ui.util.mvvm

sealed interface UiState<out T>

data object Loading : UiState<Nothing>
data object Empty : UiState<Nothing>
data class Data<T>(val state: T) : UiState<T>
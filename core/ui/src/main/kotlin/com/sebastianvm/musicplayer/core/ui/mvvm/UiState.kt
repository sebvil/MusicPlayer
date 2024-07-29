package com.sebastianvm.musicplayer.core.ui.mvvm

sealed interface UiState<out T> : State

data object Loading : UiState<Nothing>

data object Empty : UiState<Nothing>

data class Data<T>(val state: T) : UiState<T>

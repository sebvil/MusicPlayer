package com.sebastianvm.musicplayer.features.navigation

data class NavOptions(
    val popCurrent: Boolean = false,
    val presentationMode: PresentationMode = PresentationMode.Screen
) {

    enum class PresentationMode {
        Screen,
        BottomSheet
    }
}
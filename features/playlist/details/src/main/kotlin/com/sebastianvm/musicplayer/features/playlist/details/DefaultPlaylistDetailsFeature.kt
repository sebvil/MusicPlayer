package com.sebastianvm.musicplayer.features.playlist.details

import com.sebastianvm.musicplayer.core.data.di.RepositoryProvider
import com.sebastianvm.musicplayer.core.services.playback.PlaybackManager
import com.sebastianvm.musicplayer.core.ui.mvvm.MvvmComponent
import com.sebastianvm.musicplayer.core.ui.navigation.NavController
import com.sebastianvm.musicplayer.features.api.playlist.details.PlaylistDetailsArguments
import com.sebastianvm.musicplayer.features.api.playlist.details.PlaylistDetailsFeature
import com.sebastianvm.musicplayer.features.registry.FeatureRegistry

class DefaultPlaylistDetailsFeature(
    private val repositoryProvider: RepositoryProvider,
    private val playbackManager: PlaybackManager,
    private val features: FeatureRegistry,
) : PlaylistDetailsFeature {
    override fun playlistDetailsUiComponent(
        arguments: PlaylistDetailsArguments,
        navController: NavController,
    ): MvvmComponent<*, *, *> {
        return PlaylistDetailsMvvmComponent(
            arguments = arguments,
            navController = navController,
            sortPreferencesRepository = repositoryProvider.sortPreferencesRepository,
            playbackManager = playbackManager,
            playlistRepository = repositoryProvider.playlistRepository,
            features = features,
        )
    }
}

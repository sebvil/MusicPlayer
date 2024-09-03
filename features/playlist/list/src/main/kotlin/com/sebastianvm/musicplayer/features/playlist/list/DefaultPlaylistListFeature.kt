package com.sebastianvm.musicplayer.features.playlist.list

import com.sebastianvm.musicplayer.core.data.di.RepositoryProvider
import com.sebastianvm.musicplayer.core.ui.mvvm.MvvmComponent
import com.sebastianvm.musicplayer.core.ui.navigation.NavController
import com.sebastianvm.musicplayer.features.api.playlist.list.PlaylistListFeature
import com.sebastianvm.musicplayer.features.registry.FeatureRegistry

class DefaultPlaylistListFeature(
    private val repositoryProvider: RepositoryProvider,
    private val features: FeatureRegistry,
) : PlaylistListFeature {
    override fun playlistListUiComponent(navController: NavController): MvvmComponent<*, *, *> {
        return PlaylistListMvvmComponent(
            navController = navController,
            playlistRepository = repositoryProvider.playlistRepository,
            sortPreferencesRepository = repositoryProvider.sortPreferencesRepository,
            features = features,
        )
    }
}

package com.sebastianvm.musicplayer.features.album.list

import com.sebastianvm.musicplayer.core.data.di.RepositoryProvider
import com.sebastianvm.musicplayer.core.ui.mvvm.MvvmComponent
import com.sebastianvm.musicplayer.core.ui.navigation.NavController
import com.sebastianvm.musicplayer.features.api.album.list.AlbumListFeature
import com.sebastianvm.musicplayer.features.registry.FeatureRegistry

class DefaultAlbumListFeature(
    private val repositoryProvider: RepositoryProvider,
    private val features: FeatureRegistry,
) : AlbumListFeature {
    override fun albumListUiComponent(navController: NavController): MvvmComponent {
        return AlbumListMvvmComponent(
            navController = navController,
            albumRepository = repositoryProvider.albumRepository,
            sortPreferencesRepository = repositoryProvider.sortPreferencesRepository,
            features = features,
        )
    }
}

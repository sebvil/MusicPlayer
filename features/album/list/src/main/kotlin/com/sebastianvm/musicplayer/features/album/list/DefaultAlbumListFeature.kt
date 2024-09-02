package com.sebastianvm.musicplayer.features.album.list

import com.sebastianvm.musicplayer.core.data.album.AlbumRepository
import com.sebastianvm.musicplayer.core.data.preferences.SortPreferencesRepository
import com.sebastianvm.musicplayer.core.ui.mvvm.MvvmComponent
import com.sebastianvm.musicplayer.core.ui.navigation.NavController
import com.sebastianvm.musicplayer.features.api.album.list.AlbumListFeature
import com.sebastianvm.musicplayer.features.registry.FeatureRegistry

class DefaultAlbumListFeature(
    private val albumRepository: AlbumRepository,
    private val sortPreferencesRepository: SortPreferencesRepository,
    private val features: FeatureRegistry
) : AlbumListFeature {
    override fun albumListUiComponent(navController: NavController): MvvmComponent {
        return AlbumListMvvmComponent(
            navController = navController,
            albumRepository = albumRepository,
            sortPreferencesRepository = sortPreferencesRepository,
            features = features
        )
    }
}

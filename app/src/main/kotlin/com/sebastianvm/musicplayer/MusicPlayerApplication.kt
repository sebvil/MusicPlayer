package com.sebastianvm.musicplayer

import android.app.Application
import com.sebastianvm.musicplayer.core.sync.LibrarySyncWorker
import com.sebastianvm.musicplayer.features.api.album.details.AlbumDetailsFeature
import com.sebastianvm.musicplayer.features.api.album.list.AlbumListFeature
import com.sebastianvm.musicplayer.features.api.album.menu.AlbumContextMenuFeature
import com.sebastianvm.musicplayer.features.api.artist.details.ArtistDetailsFeature
import com.sebastianvm.musicplayer.features.api.artist.list.ArtistListFeature
import com.sebastianvm.musicplayer.features.api.artist.menu.ArtistContextMenuFeature
import com.sebastianvm.musicplayer.features.api.artistsmenu.ArtistsMenuFeature
import com.sebastianvm.musicplayer.features.api.genre.details.GenreDetailsFeature
import com.sebastianvm.musicplayer.features.api.genre.list.GenreListFeature
import com.sebastianvm.musicplayer.features.api.genre.menu.GenreContextMenuFeature
import com.sebastianvm.musicplayer.features.api.home.HomeFeature
import com.sebastianvm.musicplayer.features.api.navigation.NavigationHostFeature
import com.sebastianvm.musicplayer.features.api.player.PlayerFeature
import com.sebastianvm.musicplayer.features.api.playlist.details.PlaylistDetailsFeature
import com.sebastianvm.musicplayer.features.api.playlist.list.PlaylistListFeature
import com.sebastianvm.musicplayer.features.api.playlist.menu.PlaylistContextMenuFeature
import com.sebastianvm.musicplayer.features.api.playlist.tracksearch.TrackSearchFeature
import com.sebastianvm.musicplayer.features.api.queue.QueueFeature
import com.sebastianvm.musicplayer.features.api.search.SearchFeature
import com.sebastianvm.musicplayer.features.api.sort.SortMenuFeature
import com.sebastianvm.musicplayer.features.api.track.list.TrackListFeature
import com.sebastianvm.musicplayer.features.api.track.menu.TrackContextMenuFeature
import com.sebastianvm.musicplayer.features.registry.FeatureRegistry
import org.koin.android.ext.android.get
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.androidx.workmanager.dsl.worker
import org.koin.androidx.workmanager.koin.workManagerFactory
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module
import org.koin.core.context.startKoin
import org.koin.dsl.module
import org.koin.ksp.generated.module

class MusicPlayerApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger()
            androidContext(this@MusicPlayerApplication)
            workManagerFactory()
            modules(
                module { worker { LibrarySyncWorker(get(), get(), get()) } },
                ApplicationModule().module,
            )
        }

        get<FeatureRegistry>().apply {
            register(key = AlbumDetailsFeature.Key) { get<AlbumDetailsFeature>() }
            register(key = AlbumListFeature.Key) { get<AlbumListFeature>() }
            register(key = AlbumContextMenuFeature.Key) { get<AlbumContextMenuFeature>() }
            register(key = ArtistDetailsFeature.Key) { get<ArtistDetailsFeature>() }
            register(key = ArtistListFeature.Key) { get<ArtistListFeature>() }
            register(key = ArtistContextMenuFeature.Key) { get<ArtistContextMenuFeature>() }
            register(key = ArtistsMenuFeature.Key) { get<ArtistsMenuFeature>() }
            register(key = GenreDetailsFeature.Key) { get<GenreDetailsFeature>() }
            register(key = GenreListFeature.Key) { get<GenreListFeature>() }
            register(key = GenreContextMenuFeature.Key) { get<GenreContextMenuFeature>() }
            register(key = HomeFeature.Key) { get<HomeFeature>() }
            register(key = NavigationHostFeature.Key) { get<NavigationHostFeature>() }
            register(key = PlayerFeature.Key) { get<PlayerFeature>() }
            register(key = PlaylistDetailsFeature.Key) { get<PlaylistDetailsFeature>() }
            register(key = PlaylistListFeature.Key) { get<PlaylistListFeature>() }
            register(key = PlaylistContextMenuFeature.Key) { get<PlaylistContextMenuFeature>() }
            register(key = TrackSearchFeature.Key) { get<TrackSearchFeature>() }
            register(key = SearchFeature.Key) { get<SearchFeature>() }
            register(key = SortMenuFeature.Key) { get<SortMenuFeature>() }
            register(key = TrackListFeature.Key) { get<TrackListFeature>() }
            register(key = TrackContextMenuFeature.Key) { get<TrackContextMenuFeature>() }
            register(key = QueueFeature.Key) { get<QueueFeature>() }
        }
    }
}

@Module @ComponentScan("com.sebastianvm.musicplayer") class ApplicationModule

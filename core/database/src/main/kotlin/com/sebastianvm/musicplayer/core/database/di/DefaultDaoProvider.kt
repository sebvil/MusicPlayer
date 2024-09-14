package com.sebastianvm.musicplayer.core.database.di

import android.content.Context
import androidx.room.Room
import com.sebastianvm.musicplayer.core.database.MusicDatabase
import kotlinx.coroutines.CoroutineDispatcher

@Suppress("UnnecessaryAbstractClass")
abstract class DefaultDaoProvider(context: Context, ioDispatcher: CoroutineDispatcher) :
    DaoProvider by Room.databaseBuilder(context, MusicDatabase::class.java, "music_database")
        .setQueryCoroutineContext(context = ioDispatcher)
        .build()

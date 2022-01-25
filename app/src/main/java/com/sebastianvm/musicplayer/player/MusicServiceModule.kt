//package com.sebastianvm.musicplayer.player
//
//import android.content.ComponentName
//import android.content.Context
//import dagger.Module
//import dagger.Provides
//import dagger.hilt.InstallIn
//import dagger.hilt.android.qualifiers.ApplicationContext
//import dagger.hilt.components.SingletonComponent
//import javax.inject.Singleton
//
//@InstallIn(SingletonComponent::class)
//@Module
//object MusicServiceModule {
//    @Provides
//    @Singleton
//    fun provideComponentName(@ApplicationContext appContext: Context): ComponentName {
//        return ComponentName(appContext, OldMediaPlaybackService::class.java)
//    }
//}

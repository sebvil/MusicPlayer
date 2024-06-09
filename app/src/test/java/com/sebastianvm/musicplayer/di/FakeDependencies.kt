package com.sebastianvm.musicplayer.di

class FakeDependencies : Dependencies {
    override val repositoryProvider: FakeRepositoryProvider = FakeRepositoryProvider()
}

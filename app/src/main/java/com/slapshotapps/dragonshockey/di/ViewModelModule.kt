package com.slapshotapps.dragonshockey.di

import androidx.lifecycle.SavedStateHandle
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped


@Module
@InstallIn(ViewModelComponent::class)
object DetailsModule {
    @Provides
    @GameID
    @ViewModelScoped
    fun provideGameID(savedStateHandle: SavedStateHandle): Int =
        savedStateHandle.get<Int>("gameID")
            ?: throw IllegalArgumentException("You have to provide a gameID when navigating to edit a game")
}

@Module
@InstallIn(ViewModelComponent::class)
object HistoricalStatsModule {
    @Provides
    @PlayerID
    @ViewModelScoped
    fun providePlayerID(savedStateHandle: SavedStateHandle): Int =
        savedStateHandle.get<Int>("playerID")
            ?: throw IllegalArgumentException("You have to provide a playerID when navigating to historical stats")
}
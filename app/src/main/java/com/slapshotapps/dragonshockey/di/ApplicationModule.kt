package com.slapshotapps.dragonshockey.di

import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.database
import com.slapshotapps.dragonshockey.repository.HockeyRepository
import com.slapshotapps.dragonshockey.repository.HockeyRepositoryImp
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers


@Module
@InstallIn(SingletonComponent::class)
class ApplicationModule {

    @Provides
    fun provideDatabase() = Firebase.database

    @Provides
    fun provideAuth() = Firebase.auth

    @Provides
    fun providesHockeyRepo(database: FirebaseDatabase, auth: FirebaseAuth) : HockeyRepository = HockeyRepositoryImp(database, auth)

    @IoDispatcher
    @Provides
    fun providesIoDispatcher(): CoroutineDispatcher = Dispatchers.IO

    @MainDispatcher
    @Provides
    fun providesMainDispatcher(): CoroutineDispatcher = Dispatchers.Main
}
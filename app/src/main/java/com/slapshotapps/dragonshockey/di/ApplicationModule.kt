package com.slapshotapps.dragonshockey.di

import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.database
import com.slapshotapps.dragonshockey.repository.AdminRepository
import com.slapshotapps.dragonshockey.repository.AdminRepositoryImp
import com.slapshotapps.dragonshockey.repository.AuthenticationManager
import com.slapshotapps.dragonshockey.repository.AuthenticationManagerImp
import com.slapshotapps.dragonshockey.repository.GameResultRepository
import com.slapshotapps.dragonshockey.repository.GameResultRepositoryImp
import com.slapshotapps.dragonshockey.repository.HistoricalStatRepository
import com.slapshotapps.dragonshockey.repository.HistoricalStatRepositoryImp
import com.slapshotapps.dragonshockey.repository.RosterRepository
import com.slapshotapps.dragonshockey.repository.RosterRepositoryImp
import com.slapshotapps.dragonshockey.repository.ScheduleRepository
import com.slapshotapps.dragonshockey.repository.ScheduleRepositoryImp
import com.slapshotapps.dragonshockey.repository.SeasonStatRepository
import com.slapshotapps.dragonshockey.repository.SeasonStatsRepositoryImp
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
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
    fun providesAuthManager(auth:FirebaseAuth) : AuthenticationManager = AuthenticationManagerImp(auth)

    @Provides
    fun providesRosterRepo(database: FirebaseDatabase, authenticationManager: AuthenticationManager) : RosterRepository = RosterRepositoryImp(database, authenticationManager)

    @Provides
    fun providesScheduleRepo(database: FirebaseDatabase, authenticationManager: AuthenticationManager,
                             @IoDispatcher dispatcher: CoroutineDispatcher) : ScheduleRepository = ScheduleRepositoryImp(database, authenticationManager, dispatcher)

    @Provides
    fun providesGameResultRepo(database: FirebaseDatabase, authenticationManager: AuthenticationManager) : GameResultRepository = GameResultRepositoryImp(database, authenticationManager)

    @Provides
    fun providesSeasonStatsRepo(database: FirebaseDatabase, authenticationManager: AuthenticationManager) : SeasonStatRepository = SeasonStatsRepositoryImp(database, authenticationManager)

    @IoDispatcher
    @Provides
    fun providesIoDispatcher(): CoroutineDispatcher = Dispatchers.IO

    @MainDispatcher
    @Provides
    fun providesMainDispatcher(): CoroutineDispatcher = Dispatchers.Main

    @Provides
    fun providesAdminRepo(database: FirebaseDatabase) : AdminRepository = AdminRepositoryImp(database)

    @Provides
    fun providesHistoricalStatRepo(database: FirebaseDatabase, authenticationManager: AuthenticationManager) : HistoricalStatRepository = HistoricalStatRepositoryImp(database, authenticationManager)
}
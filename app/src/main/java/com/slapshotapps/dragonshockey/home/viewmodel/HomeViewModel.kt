package com.slapshotapps.dragonshockey.home.viewmodel

import androidx.lifecycle.ViewModel
import com.slapshotapps.dragonshockey.di.IoDispatcher
import com.slapshotapps.dragonshockey.repository.RosterRepository
import com.slapshotapps.dragonshockey.repository.RosterResult
import com.slapshotapps.dragonshockey.repository.ScheduleRepository
import com.slapshotapps.dragonshockey.repository.ScheduleResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val rosterRepository: RosterRepository,
                                        private val scheduleRepository: ScheduleRepository,
                                        @IoDispatcher private val ioDispatcher: CoroutineDispatcher) : ViewModel()
{
//    val rosterFlow = hockeyRepository.getRoster()

    suspend fun getRoster(): Flow<RosterResult>{
        rosterRepository.authenticateUser()

        return rosterRepository.getRoster()
    }

    suspend fun getSchedule(): Flow<ScheduleResult>{
        rosterRepository.authenticateUser()

        return scheduleRepository.getSchedule()
    }


}
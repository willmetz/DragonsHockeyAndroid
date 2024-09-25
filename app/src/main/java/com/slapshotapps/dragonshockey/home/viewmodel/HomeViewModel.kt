package com.slapshotapps.dragonshockey.home.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.slapshotapps.dragonshockey.di.IoDispatcher
import com.slapshotapps.dragonshockey.repository.HockeyRepository
import com.slapshotapps.dragonshockey.repository.RosterResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

@HiltViewModel
class HomeViewModel @Inject constructor(private val hockeyRepository: HockeyRepository,
                                        @IoDispatcher private val ioDispatcher: CoroutineDispatcher) : ViewModel()
{
//    val rosterFlow = hockeyRepository.getRoster()

    suspend fun getRoster(): Flow<RosterResult>{
        hockeyRepository.authenticateUser()

        return hockeyRepository.getRoster()
    }


}
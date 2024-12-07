package com.slapshotapps.dragonshockey.stats

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.slapshotapps.dragonshockey.repository.SeasonStatRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

sealed interface SeasonStatScreenState{
    data object loading : SeasonStatScreenState
    data class onError(val message: String) : SeasonStatScreenState

}

@HiltViewModel
class SeasonStatsViewModel @Inject constructor(private val seasonStatRepository: SeasonStatRepository) : ViewModel() {
    val seasonStatsState : StateFlow<SeasonStatScreenState> = seasonStatRepository.getSeasonStats().map {
        SeasonStatScreenState.loading
    }.stateIn(viewModelScope, SharingStarted.Lazily, SeasonStatScreenState.loading)
}
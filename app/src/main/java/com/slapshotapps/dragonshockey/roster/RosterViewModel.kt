package com.slapshotapps.dragonshockey.roster

import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.toUpperCase
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.slapshotapps.dragonshockey.extensions.string.titleCase
import com.slapshotapps.dragonshockey.models.Player
import com.slapshotapps.dragonshockey.repository.RosterRepository
import com.slapshotapps.dragonshockey.repository.RosterResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject


data class RosterPlayer(val position: String, val name: String, val number: String)

sealed interface RosterScreenState{
    data object Loading : RosterScreenState
    data object RosterUnavailable: RosterScreenState
    data class HasRoster(val players: List<RosterPlayer>): RosterScreenState
}

@HiltViewModel
class RosterViewModel @Inject constructor(rosterRepository: RosterRepository) : ViewModel() {

    val rosterState : StateFlow<RosterScreenState> = rosterRepository.getRoster().map { rosterResult ->
        when(rosterResult){
            is RosterResult.HasRoster -> RosterScreenState.HasRoster(buildRoster(rosterResult.players))
            RosterResult.NoRoster -> RosterScreenState.RosterUnavailable
            RosterResult.RosterError -> RosterScreenState.RosterUnavailable
        }
    }.stateIn(viewModelScope, SharingStarted.Lazily, RosterScreenState.Loading)


    private fun buildRoster(players: List<Player>) = players.map {
        RosterPlayer(getPosition(it.position), getRosterName(it.firstName, it.lastName), it.number) }.sortedBy { it.number }

    private fun getPosition(positionValue: String) = when(positionValue.toUpperCase(Locale.current).trim()){
        "D" -> "Defense"
        "G" -> "Goalie"
        else -> "Forward"
    }

    private fun getRosterName(firstName: String, lastName: String) = "${firstName.titleCase()} ${lastName.titleCase()}"
}

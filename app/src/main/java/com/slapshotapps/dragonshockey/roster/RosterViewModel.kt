package com.slapshotapps.dragonshockey.roster


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.slapshotapps.dragonshockey.extensions.primitives.titleCase
import com.slapshotapps.dragonshockey.models.Player
import com.slapshotapps.dragonshockey.models.PlayerPosition
import com.slapshotapps.dragonshockey.repository.RosterRepository
import com.slapshotapps.dragonshockey.repository.RosterResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

data class RosterPlayer(val position: String, val name: String, val number: String)

sealed interface RosterElement{
    data class RosterMember(val player: RosterPlayer): RosterElement
    data class Header(val headerLabel: String): RosterElement
}

sealed interface RosterScreenState{
    data object Loading : RosterScreenState
    data object RosterUnavailable: RosterScreenState
    data class HasRoster(val rosterDetails: List<RosterElement>): RosterScreenState
}

@HiltViewModel
class RosterViewModel @Inject constructor(rosterRepository: RosterRepository) : ViewModel() {

    companion object {
        const val DEFENSE = "Defense"
        const val FORWARD = "Forward"
        const val GOALIE = "Goalie"
    }

    val rosterState: StateFlow<RosterScreenState> =
        rosterRepository.getRoster().map { rosterResult ->
            when (rosterResult) {
                is RosterResult.HasRoster -> RosterScreenState.HasRoster(buildRoster(rosterResult.players))
                RosterResult.NoRoster -> RosterScreenState.RosterUnavailable
                RosterResult.RosterError -> RosterScreenState.RosterUnavailable
            }
        }.stateIn(viewModelScope, SharingStarted.Lazily, RosterScreenState.Loading)


    private fun buildRoster(players: List<Player>) : List<RosterElement> {
        return orderRoster( players.map {
            RosterPlayer(
                getPosition(it.position),
                getRosterName(it.firstName, it.lastName),
                it.number
            )
        }.groupBy { it.position })
    }

    private fun orderRoster(playersByPosition: Map<String, List<RosterPlayer>>) : List<RosterElement>{
        val sortedRoster = mutableListOf<RosterElement>()

        sortedRoster.add(RosterElement.Header("Forward"))
        playersByPosition.getOrDefault(FORWARD, null)
            ?.let { rosterPlayers -> sortedRoster.addAll(rosterPlayers.sortedBy { it.number.toIntOrNull() }.map { RosterElement.RosterMember(it) }) }

        sortedRoster.add(RosterElement.Header("Defense"))
        playersByPosition.getOrDefault(DEFENSE, null)
            ?.let { rosterPlayers -> sortedRoster.addAll(rosterPlayers.sortedBy { it.number.toIntOrNull() }.map { RosterElement.RosterMember(it) }) }

        sortedRoster.add(RosterElement.Header("Goalie"))
        playersByPosition.getOrDefault(GOALIE, null)
            ?.let { rosterPlayers -> sortedRoster.addAll(rosterPlayers.sortedBy { it.number.toIntOrNull() }.map { RosterElement.RosterMember(it) }) }

        return sortedRoster
    }

    private fun getPosition(position: PlayerPosition) = when(position){
        PlayerPosition.Defense -> DEFENSE
        PlayerPosition.Goalie -> GOALIE
        PlayerPosition.Forward -> FORWARD
    }

    private fun getRosterName(firstName: String, lastName: String) = "${firstName.titleCase()} ${lastName.titleCase()}"


}

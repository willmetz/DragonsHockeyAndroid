package com.slapshotapps.dragonshockey.repository

import androidx.annotation.Keep
import com.google.android.gms.tasks.Task
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.FirebaseDatabase
import com.slapshotapps.dragonshockey.admin.editgamestats.PlayerEditGameStats
import com.slapshotapps.dragonshockey.constants.DATABASE_GAME_RESULT_KEY
import com.slapshotapps.dragonshockey.constants.DATABASE_GAME_STATS_KEY
import com.slapshotapps.dragonshockey.extensions.primitives.toIntOrZero
import kotlinx.coroutines.isActive
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine


interface AdminRepository {
    suspend fun onUpdateGameResult(teamScore: Int, opponentScore: Int, overtimeLoss: Boolean, gameID: Int)
    suspend fun onUpdateGameStats(gameID: Int, updatedStats: List<PlayerEditGameStats> )
}

class AdminRepositoryImp @Inject constructor(private val database: FirebaseDatabase) : AdminRepository{
    override suspend fun onUpdateGameResult(teamScore: Int, opponentScore: Int,
                                            overtimeLoss: Boolean, gameID: Int) {
        //get all results available
       val key = getGameResultKey(database.reference.child(DATABASE_GAME_RESULT_KEY).get(), gameID)

        //see if the game ID exists
        if(key != null){
            updateExistingGameResult(teamScore, opponentScore, overtimeLoss, key)
        }else{
            addNewResult(teamScore, opponentScore, overtimeLoss, gameID)
        }
    }

    override suspend fun onUpdateGameStats(gameID: Int, updatedStats: List<PlayerEditGameStats> ){
        val gameStatsKey = getGameStatsKey(database.reference.child(DATABASE_GAME_STATS_KEY).get(), gameID)

        //see if the game ID exists
        if(gameStatsKey != null){
            updateExistingGameStats(gameID, updatedStats, gameStatsKey)
        }else{
            addNewGameStats(gameID, updatedStats)
        }
    }

    @Keep
    data class GameStatsDatabaseObject(val gameID: Int, val stats: List<StatsDatabaseObject>)
    @Keep
    data class StatsDatabaseObject(val assists: Int, val goals: Int, val goalsAgainst: Int,
                                         val penaltyMinutes: Int, val playerID: Int, val present: Boolean)

    private fun buildDBStatsObject(gameID: Int, updatedStats: List<PlayerEditGameStats>): GameStatsDatabaseObject{
        return GameStatsDatabaseObject(gameID, updatedStats.map {
            when(it){
                is PlayerEditGameStats.GoalieStats -> StatsDatabaseObject(it.assists.toIntOrZero(),
                    0, it.goalsAgainst.toIntOrZero(), it.penaltyMins.toIntOrNull() ?:0,
                    it.playerID, it.present)
                is PlayerEditGameStats.SkaterStats -> StatsDatabaseObject(it.assists.toIntOrZero(),
                    it.goals.toIntOrZero(), 0, it.penaltyMins.toIntOrZero(),
                    it.playerID, it.present)
            }
        })
    }


    private fun addNewGameStats(gameID: Int, updatedStats: List<PlayerEditGameStats>){
        database.reference.child(DATABASE_GAME_STATS_KEY).apply {
            push().key?.let {key -> this.child(key).setValue(buildDBStatsObject(gameID, updatedStats)) }
        }
    }

    private fun updateExistingGameStats(gameID: Int, updatedStats: List<PlayerEditGameStats>, key: String){
        database.reference.child(DATABASE_GAME_STATS_KEY).child(key).setValue(buildDBStatsObject(gameID, updatedStats))
    }



    private fun updateExistingGameResult(teamScore: Int, opponentScore: Int, overtimeLoss: Boolean, key: String){
        database.reference.child(DATABASE_GAME_RESULT_KEY).child(key).apply {
            child("dragonsScore").setValue(teamScore)
            child("opponentScore").setValue(opponentScore)
            child("overtimeLoss").setValue(overtimeLoss)
        }
    }

    @Keep
    data class GameResultDatabaseObject(val dragonsScore: Int?, val gameID: Int?, val opponentScore: Int?, val overtimeLoss: Boolean?)
    
    private fun addNewResult(teamScore: Int, opponentScore: Int, overtimeLoss: Boolean, gameID: Int){
        val result = GameResultDatabaseObject(teamScore, gameID, opponentScore, overtimeLoss)

        database.reference.child(DATABASE_GAME_RESULT_KEY).apply {
            push().key?.let {key -> this.child(key).setValue(result) }
        }
    }

    private suspend fun getGameResultKey(task: Task<DataSnapshot>, gameID: Int) = suspendCoroutine<String?> { continuation ->
        task.addOnSuccessListener {
            val key = it.children.firstOrNull { snapShot ->
                val actualID = (snapShot.value as? Map<*, *>)?.get("gameID") as? Long
                actualID == gameID.toLong()
            }

            continuation.takeIf { it.context.isActive }?.resume(key?.key)
        }.addOnFailureListener{
            continuation.takeIf { it.context.isActive }?.resume(null)
        }
    }

    private suspend fun getGameStatsKey(task: Task<DataSnapshot>, gameID: Int) = suspendCoroutine<String?> {continuation ->
        task.addOnSuccessListener {
            val key = it.children.firstOrNull {
                val actualID = (it.value as? Map<*, *>)?.get("gameID") as? Long
                actualID == gameID.toLong()

            }
            continuation.takeIf { it.context.isActive }?.resume(key?.key)
        }.addOnFailureListener{
            continuation.takeIf { it.context.isActive }?.resume(null)
        }
    }

}
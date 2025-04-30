package com.slapshotapps.dragonshockey.repository

import com.google.android.gms.tasks.Task
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.FirebaseDatabase
import com.google.gson.annotations.SerializedName
import com.slapshotapps.dragonshockey.constants.DATABASE_GAME_RESULT_KEY
import com.slapshotapps.dragonshockey.network.models.GameResultDTO
import kotlinx.coroutines.isActive
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine


interface AdminRepository {
    suspend fun onUpdateGameResult(teamScore: Int, opponentScore: Int, overtimeLoss: Boolean, gameID: Int)
}

class AdminRepositoryImp @Inject constructor(private val database: FirebaseDatabase) : AdminRepository{
    override suspend fun onUpdateGameResult(teamScore: Int, opponentScore: Int,
                                            overtimeLoss: Boolean, gameID: Int) {
        //get all results available
       val key = getGameResultKey(database.getReference().child(DATABASE_GAME_RESULT_KEY).get(), gameID)

        //see if the game ID exists
        if(key != null){
            updateExistingGameResult(teamScore, opponentScore, overtimeLoss, key)
        }else{
            addNewResult(teamScore, opponentScore, overtimeLoss, gameID)
        }
    }

    private fun updateExistingGameResult(teamScore: Int, opponentScore: Int, overtimeLoss: Boolean, key: String){
        database.getReference().child(DATABASE_GAME_RESULT_KEY).child(key).apply {
            child("dragonsScore").setValue(teamScore)
            child("opponentScore").setValue(opponentScore)
            child("overtimeLoss").setValue(overtimeLoss)
        }
    }

    data class GameResultDatabaseObject(val dragonsScore: Int?, val gameID: Int?, val opponentScore: Int?, val overtimeLoss: Boolean?)
    private fun addNewResult(teamScore: Int, opponentScore: Int, overtimeLoss: Boolean, gameID: Int){
        val result = GameResultDatabaseObject(teamScore, gameID, opponentScore, overtimeLoss)

        database.getReference().child(DATABASE_GAME_RESULT_KEY).apply {
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



}
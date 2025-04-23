package com.slapshotapps.dragonshockey.repository

import com.google.android.gms.tasks.Task
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.isActive
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine


interface AdminRepository {
    suspend fun onUpdateGameResult(teamScore: String, opponentScore: String, overtimeLoss: Boolean, gameID: Int)
}

class AdminRepositoryImp @Inject constructor(private val database: FirebaseDatabase) : AdminRepository{
    override suspend fun onUpdateGameResult(teamScore: String, opponentScore: String,
                                            overtimeLoss: Boolean, gameID: Int) {
        //get all results available
       val key = getGameResultKey(database.getReference().child("gameResults").get(), gameID)

        //see if the game ID exists
        if(key != null){
            println("key exists for $gameID")
        }else{
            println("no key for $gameID")
        }
    }

    private suspend fun getGameResultKey(task: Task<DataSnapshot>, gameID: Int) = suspendCoroutine<String?> { continuation ->
        task.addOnSuccessListener {
            val key = it.children.firstOrNull { snapShot ->
                val actualID = (snapShot.value as? Map<String, Any>)?.get("gameID") as? Long
                actualID == gameID.toLong()
            }

            continuation.takeIf { it.context.isActive }?.resume(key?.key)
        }.addOnFailureListener{
            continuation.takeIf { it.context.isActive }?.resume(null)
        }
    }



}
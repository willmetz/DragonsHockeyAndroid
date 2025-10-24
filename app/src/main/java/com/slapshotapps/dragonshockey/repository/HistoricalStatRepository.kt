package com.slapshotapps.dragonshockey.repository

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.slapshotapps.dragonshockey.constants.DATABASE_HISTORICAL_STATS_KEY
import javax.inject.Inject


interface HistoricalStatRepository {
    suspend fun getHistoricalStats(playerID: String)
}

sealed interface HistoricalStatResult{
    data object Error : HistoricalStatResult
    data object NoResults : HistoricalStatResult
    data class HasResults(val test:Boolean) : HistoricalStatResult
}

class HistoricalStatRepositoryImp @Inject constructor(private val database: FirebaseDatabase, private  val authenticationManager: AuthenticationManager) : HistoricalStatRepository{
    override suspend fun getHistoricalStats(playerID: String){
        authenticationManager.authenticateUserAnonymously()

        val postListener = object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                println("error")
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                println("${dataSnapshot.exists()}")
            }
        }

        database.getReference(DATABASE_HISTORICAL_STATS_KEY).addValueEventListener(postListener)

//        awaitClose{
//            database.getReference(DATABASE_GAME_STATS_KEY).removeEventListener(postListener)
//        }
    }

}
package com.slapshotapps.dragonshockey.network.models

import com.google.gson.annotations.SerializedName


data class GameResultDTO(@SerializedName("dragonsScore") val teamScore: Int?,
                         @SerializedName("gameID") val id: Int?,
                         @SerializedName("opponentScore") val opponentScore: Int?,
                         @SerializedName("overtimeLoss") val overtimeLoss: Boolean?)
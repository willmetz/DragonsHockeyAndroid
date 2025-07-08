package com.slapshotapps.dragonshockey.network.models

import com.google.gson.annotations.SerializedName


data class GameDTO(@SerializedName("gameID") val gameID: Int?,
                   @SerializedName("gameTime") val gameTime: String?,
                   @SerializedName("home") val home: Boolean?,
                   @SerializedName("opponent") val opponent: String?,
                   @SerializedName("rink") val rink: String?)
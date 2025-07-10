package com.slapshotapps.dragonshockey.network.models

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class PlayerDTO(@SerializedName("firstName") val firstName: String?,
                  @SerializedName("lastName") val lastName: String?,
                  @SerializedName("number") val number: Long?,
                     @SerializedName("position") val position: String?,
                  @SerializedName("playerID") val playerID: Long?,
                  @SerializedName("shot") val shot: String?)
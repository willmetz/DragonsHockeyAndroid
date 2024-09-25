package com.slapshotapps.dragonshockey.models

import com.google.firebase.database.PropertyName


data class Player(val firstName: String,
                  val lastName: String,
                  val number: String,
                  val playerID: Int,
                  val position: String,
                  val shot: String)

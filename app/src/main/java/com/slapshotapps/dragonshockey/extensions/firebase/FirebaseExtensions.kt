package com.slapshotapps.dragonshockey.extensions.firebase

import com.google.firebase.database.DataSnapshot
import com.google.gson.Gson
import com.slapshotapps.dragonshockey.extensions.gson.decodeFromMap
import com.slapshotapps.dragonshockey.network.models.PlayerDTO


inline fun <reified T> DataSnapshot.toList(gson: Gson) : List<T?> {
    return kotlin.runCatching {
        this.children.map { ds ->
            ds.value?.let {
                gson.decodeFromMap<T>(it)
            }
        }
    }.getOrNull() ?: listOf()
}
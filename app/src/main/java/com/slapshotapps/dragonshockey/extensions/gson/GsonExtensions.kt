package com.slapshotapps.dragonshockey.extensions.gson

import com.google.gson.Gson

//
//gson.toJsonTree(it)
//.let { jsonObj -> gson.fromJson(jsonObj, PlayerDTO::class.java)

inline fun <reified T> Gson.decodeFromMap(data: Any): T {
    return this.toJsonTree(data).let { obj -> this.fromJson(obj, T::class.java) }
}
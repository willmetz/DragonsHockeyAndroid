package com.slapshotapps.dragonshockey.extensions.string

fun String.titleCase(): String{
    return this.lowercase().replaceFirstChar { it.uppercase() }
}
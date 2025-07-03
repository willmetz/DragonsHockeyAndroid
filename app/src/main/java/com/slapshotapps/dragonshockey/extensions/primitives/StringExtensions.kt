package com.slapshotapps.dragonshockey.extensions.primitives

fun String.titleCase(): String{
    return this.lowercase().replaceFirstChar { it.uppercase() }
}

fun String.toIntOrZero(): Int{
    return this.toIntOrNull() ?: 0
}
package com.sebastianvm.musicplayer.util.extensions

fun <T> MutableList<T>.swap(index1: Int, index2: Int) {
    val tmp = this[index1] // 'this' corresponds to the list
    this[index1] = this[index2]
    this[index2] = tmp
}

fun <T> MutableList<T>.move(from: Int, to: Int) {
    val tmp = removeAt(from)
    add(to, tmp)
}
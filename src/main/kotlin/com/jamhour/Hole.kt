package com.jamhour

/**
 * Represents a hole or free space in memory.
 *
 * @property base The starting address of the hole in memory.
 * @property limit The ending address of the hole in memory.
 *
 * @param base An integer representing the starting address of the hole.
 * @param limit An integer representing the ending address of the hole.
 */
data class Hole(val base: Int, val limit: Int) {

    fun size() = limit - base

}
package com.jamhour.model

/**
 * Represents a hole or free space in memory.
 *
 * @property base The starting address of the hole in memory.
 * @property limit The ending address of the hole in memory.
 *
 * @param base An integer representing the starting address of the hole.
 * @param limit An integer representing the ending address of the hole.
 */
@JvmRecord
data class Hole(val base: Int, val limit: Int, val nameOfHole: String) : Comparable<Hole> {

    private constructor(size: Int) : this(0, size, "hole of size $size")

    fun size() = limit - base
    fun isExactFit(process: Process) = size() == process.size
    fun getRemainingSpaceAfter(process: Process) = size() - process.size
    override fun compareTo(other: Hole) = size().compareTo(other.size())

    companion object {
        fun ofSize(process: Process) = Hole(process.size)
    }
}
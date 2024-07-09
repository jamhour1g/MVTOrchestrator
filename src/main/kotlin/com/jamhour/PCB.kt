package com.jamhour


val OS_PCB = PCB(OS_PROCESS, 0)

/**
 * Represents a Process Control Block (PCB), which contains information about a process
 * and its memory allocation.
 *
 * @property base The starting address of the memory allocated to the process.
 * @property limit The boundary address of the memory allocated to the process.
 * @property process The Process object associated with this PCB.
 *
 * @param base An integer representing the starting address of the allocated memory.
 * @param limit An integer representing The boundary address of the allocated memory.
 * @param process A Process object containing the details of the process.
 *
 * The PCB class is used to keep track of the memory allocation and process information
 * in a memory management system. It combines the memory allocation details (base and limit)
 * with the process information.
 *
 */
@JvmRecord
data class PCB(val process: Process, val base: Int, val limit: Int = base + process.size) : Comparable<PCB> {

    override fun compareTo(other: PCB): Int {
        return this.process.compareTo(other.process)
    }

}
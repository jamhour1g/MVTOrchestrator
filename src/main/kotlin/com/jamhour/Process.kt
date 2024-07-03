package com.jamhour

/**
 * Represents a computational process with its characteristics.
 *
 * @property id The unique identifier for the process.
 * @property size The memory size required by the process, typically measured in megabytes (MB).
 * @property time The execution time of the process, usually expressed in milliseconds.
 *
 * @param id An integer representing the unique identifier of the process.
 * @param size An integer representing the memory size required by the process.
 * @param time An integer representing the execution time of the process.
 *
 * @sample
 * val process = Process(1, 1024, 5000)
 * println("Process ${process.id} requires ${process.size} MB and takes ${process.time} ms to execute.")
 */
data class Process(val id: Int, val size: Int, val time: Int)
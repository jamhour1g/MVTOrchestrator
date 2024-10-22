package com.jamhour.util

import com.jamhour.model.Process
import kotlinx.coroutines.coroutineScope
import java.nio.file.Files
import java.nio.file.Path
import java.util.logging.Logger
import java.util.regex.Pattern
import kotlin.io.useLines
import kotlin.sequences.filter
import kotlin.sequences.map
import kotlin.sequences.toList
import kotlin.text.toInt

/**
 * Regular expression pattern to match process information in the input file.
 * The pattern expects three space-separated integer values for process ID, size, and time.
 */
val linePattern: Pattern =
    Pattern.compile("(?<processId>\\w+)\\s+(?<processSize>\\d+)\\s+(?<processTime>\\d+)\\s*")

/**
 * Logger instance for the FileReader.
 */
private val logger = Logger.getLogger("FileReader")

/**
 * Reads a file containing process information and returns a list of Process objects.
 *
 * @param filePath The path to the file to be read.
 * @return A list of Process objects parsed from the file. Returns an empty list if the file cannot be read or parsed.
 */
suspend fun readFile(filePath: Path): List<Process> = coroutineScope {
    runCatching {
        Files.newBufferedReader(filePath).useLines { seq ->
            return@runCatching seq.map { linePattern.matcher(it) }
                .filter { it.matches() }
                .map {
                    val processId = it.group("processId")!!
                    val processSize = it.group("processSize")!!.toInt()
                    val processTime = it.group("processTime")!!.toInt()
                    Process(processId, processSize, processTime).also { logger.fine { "Created process: $it" } }
                }
                .toList().also { logger.fine { "Created list of processes with size ${it.size}" } }
        }
    }.getOrElse { emptyList<Process>().also { logger.severe { "Failed to read file: $it with error: $it returning an empty list" } } }
}
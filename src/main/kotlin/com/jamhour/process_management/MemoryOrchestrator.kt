package com.jamhour.process_management

import com.jamhour.util.readFile
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import java.nio.file.Path

class MemoryOrchestrator private constructor() {

    val jobQueue = JobQueue()
    lateinit var readyProcesses: ReadyQueue; private set

    private suspend fun readFiles(
        readyFile: Path,
        jobFile: Path
    ) = coroutineScope {
        val readyProcess = async {
            readFile(readyFile)
        }
        val jobQueueJobs = async {
            readFile(jobFile)
        }
        readyProcess.await().also {
            readyProcesses = ReadyQueue.build {
                addAll(it)
            }
        }
        jobQueueJobs.await().also { jobQueue.addAll(it) }
    }

    fun isEmpty() = readyProcesses.isEmpty() && jobQueue.isEmpty()

    fun removeScheduling(): Boolean {
        val removed = readyProcesses.remove()
        removed?.let {
            jobQueue.poll()?.let {
                val add = readyProcesses.add(it)
                if (!add) {
                    jobQueue.offer(it)
                    return false
                }
            }
        }
        return removed != null
    }

    class Builder {

        private var readyFile = Path.of("src/test/resources/ready.txt")
        private var jobFile = Path.of("src/test/resources/jobs_correct_format.txt")

        fun readyFilePath(readyFile: Path?) = apply {
            readyFile?.let {
                this.readyFile = readyFile
            }
        }

        fun jobFilePath(jobFile: Path?) = apply {
            jobFile?.let {
                this.jobFile = jobFile
            }
        }

        suspend fun build(): MemoryOrchestrator {
            return MemoryOrchestrator().apply {
                readFiles(readyFile, jobFile)
            }
        }
    }


}
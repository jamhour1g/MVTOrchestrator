package com.jamhour.process_management

import com.jamhour.model.Process
import java.util.ArrayDeque
import java.util.Queue

class JobQueue(
    val queue: Queue<Process> = ArrayDeque()
) : Queue<Process> by queue {

    override fun toString(): String {
        return queue.toString()
    }
}
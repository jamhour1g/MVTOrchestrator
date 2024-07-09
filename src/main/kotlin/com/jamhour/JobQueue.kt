package com.jamhour

import java.util.ArrayDeque
import java.util.Queue

class JobQueue(
    val queue: Queue<Process> = ArrayDeque()
) : Queue<Process> by queue {

    override fun toString(): String {
        return queue.toString()
    }
}
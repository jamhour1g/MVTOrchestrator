package com.jamhour

import com.jamhour.model.Hole
import com.jamhour.model.OS_PCB
import com.jamhour.model.PCB
import com.jamhour.model.Process
import java.util.NavigableSet
import java.util.PriorityQueue
import java.util.Queue
import java.util.SequencedSet
import java.util.TreeSet


class ReadyQueue(
    private val readyProcesses: Queue<PCB> = PriorityQueue(),
    private val recentProcesses: SequencedSet<PCB> = LinkedHashSet(),
    private val availableHoles: NavigableSet<Hole> = TreeSet(),
    val operatingSystemPcb: PCB = OS_PCB,
    val maximumSize: Int = 2048
) : Iterable<PCB> {

    private var currentSize = 0

    init {
        operatingSystemPcb.addToQueue()
    }

    // PRIVATE FUNCTIONS
    private fun hasReachedMaxSize() = remainingSize() == 0
    private fun Process.hasSufficientSpace() = maximumSize >= this.size + currentSize
    private fun Hole.removeFromHoles() = availableHoles.remove(this)
    private fun Hole.addToAvailableHoles() = availableHoles.add(this)

    private fun PCB.updateCurrentSize() {
        currentSize += process.size
    }

    private fun PCB.addToQueue(): Boolean {
        updateCurrentSize()
        readyProcesses.add(this)
        return recentProcesses.add(this)
    }

    // PUBLIC FUNCTIONS
    fun remainingSize() = maximumSize - currentSize
    fun size() = readyProcesses.size
    fun holesSize() = availableHoles.size   // TODO : Test this
    fun isEmpty() = size() == 1 && readyProcesses.contains(operatingSystemPcb)
    fun addAll(collection: Collection<Process>): Boolean = collection.map { add(it) }.all { it }

    fun add(process: Process): Boolean {

        if (process == OS_PCB.process) {
            return true
        }

        if (isEmpty() && process.hasSufficientSpace()) {
            return PCB(process, operatingSystemPcb.limit).addToQueue()
        }

        if (hasReachedMaxSize() || !process.hasSufficientSpace()) {
            return false
        }

        val suitableHole = availableHoles.ceiling(Hole.ofSize(process))

        if (suitableHole == null) {
            return PCB(process, recentProcesses.last.limit).addToQueue()
        }

        suitableHole.removeFromHoles()

        if (suitableHole.isExactFit(process)) {
            val perfectlyFitHole = suitableHole
            return PCB(process, perfectlyFitHole.base).addToQueue()
        }

        val largerHole = suitableHole
        val leftoverSpace = largerHole.getRemainingSpaceAfter(process)

        val pcb = PCB(process, largerHole.base).also { it.addToQueue() }
        Hole(pcb.limit, pcb.limit + leftoverSpace).also { it.addToAvailableHoles() }

        return true

    }


    override fun iterator(): Iterator<PCB> = readyProcesses.iterator()
    fun recentProcessesIterator(): Iterable<PCB> = recentProcesses
    fun availableHolesIterator(): Iterable<Hole> = availableHoles  // TODO : Test this


}
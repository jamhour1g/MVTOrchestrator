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
    val maximumSize: Int = 2048,
    val numberOfHolesShouldNotExceed: Int = 3
) : Iterable<PCB> {

    var currentSize = 0; private set
    var numOfCompactions = 0; private set

    init {
        if (readyProcesses.isNotEmpty() || recentProcesses.isNotEmpty() || availableHoles.isNotEmpty()) {
            error("ReadyQueue should be empty upon creation")
        }
        operatingSystemPcb.addToQueue()
    }

    // PRIVATE FUNCTIONS
    private fun hasReachedMaxSize() = remainingSize() == 0
    private fun Process.hasSufficientSpace() = maximumSize >= this.size + currentSize
    private fun Hole.removeFromHoles() = availableHoles.remove(this)
    private fun Hole.addToAvailableHoles() = availableHoles.add(this)
    private fun PCB.remove(): PCB {
        assert(this != OS_PCB) { "OS PCB cannot be removed" }
        recentProcesses.remove(this)
        Hole(base, limit).addToAvailableHoles()
        currentSize -= process.size
        return this
    }

    private fun MutableCollection<PCB>.refill(collection: Collection<PCB>) {
        clear()
        addAll(collection)
    }

    private fun PCB.shiftLeft(hole: Hole) = copy(
        base = base - hole.size(),
        limit = limit - hole.size()
    )

    private fun PCB.updateCurrentSize() {
        currentSize += process.size
    }

    private fun PCB.addToQueue(): Boolean {
        updateCurrentSize()
        readyProcesses.add(this)
        return recentProcesses.add(this)
    }

    private fun repositionPcbsForHole(hole: Hole, fromCollection: Collection<PCB>) = buildList {
        add(operatingSystemPcb)
        for (item in fromCollection) {
            if (item == operatingSystemPcb) {
                continue
            }
            if (hole.base > item.base) {
                add(item)
                continue
            }
            add(item.shiftLeft(hole))
        }
    }

    private fun compactHoles() {

        availableHoles.forEach {
            readyProcesses.refill(repositionPcbsForHole(it, readyProcesses))
            recentProcesses.refill(repositionPcbsForHole(it, recentProcesses))
        }

        availableHoles.clear()
        Hole(recentProcesses.last.limit, remainingSize()).addToAvailableHoles()

        numOfCompactions++
    }

    private fun fillRemainingSpace() = Hole(recentProcesses.last.limit, remainingSize()).addToAvailableHoles()

    private fun handleSuitableHole(process: Process): Boolean {
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

    // PUBLIC FUNCTIONS
    fun remainingSize() = maximumSize - currentSize
    fun size() = readyProcesses.size
    fun holesSize() = availableHoles.size
    fun isEmpty() = size() == 1 && readyProcesses.contains(operatingSystemPcb)
    fun addAll(collection: Collection<Process>, fillRemainingSpace: Boolean = true) {
        val allAdded = collection.all { add(it) }

        if (fillRemainingSpace && allAdded && !hasReachedMaxSize()) {
            fillRemainingSpace()
        }
    }

    fun add(process: Process) = when {
        process == OS_PCB.process -> true
        !process.hasSufficientSpace() || hasReachedMaxSize() -> false
        isEmpty() -> PCB(process, operatingSystemPcb.limit).addToQueue()
        else -> handleSuitableHole(process)
    }

    fun remove() = when {
        isEmpty() -> null
        else -> {
            val removed = readyProcesses.poll().remove()
            if (holesSize() > numberOfHolesShouldNotExceed) {
                compactHoles()
            }
            removed
        }
    }

    fun removeAll(): Boolean {
        var removed = false
        while (!isEmpty()) {
            remove().let { removed = true }
        }
        return removed
    }

    fun clear() {
        readyProcesses.clear()
        recentProcesses.clear()
        availableHoles.clear()
        operatingSystemPcb.addToQueue()
        currentSize = 0
        numOfCompactions = 0
    }

    override fun toString() = readyProcesses.toString()
    override fun iterator() = readyProcesses.iterator()
    fun recentProcesses() = recentProcesses.toList()
    fun availableHoles() = availableHoles.toList()

}
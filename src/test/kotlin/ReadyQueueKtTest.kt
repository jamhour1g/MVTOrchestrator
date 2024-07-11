import com.jamhour.ReadyQueue
import com.jamhour.model.Hole
import com.jamhour.model.OS_PCB
import com.jamhour.model.OS_PROCESS
import com.jamhour.model.PCB
import com.jamhour.model.Process
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.assertThrows
import java.lang.IllegalStateException
import java.util.PriorityQueue
import java.util.TreeSet
import kotlin.test.Test
import kotlin.test.assertEquals

class ReadyQueueKtTest {

    private val expectedNumberOfCompactions = 1
    private val expectedNumberOfHoleWhenAllRemoved = 2
    private val processes = listOf(
        OS_PROCESS,
        Process("1", 521, 10), // should be removed second
        Process("2", 100, 5),  // should be removed first
        Process("3", 240, 16), // should be removed fifth
        Process("4", 300, 22), // should be removed fourth
        Process("6", 375, 12)  // should be removed third
    )

    private val expectedResults = listOf(
        OS_PCB,
        PCB(Process("1", 521, 10), 512),
        PCB(Process("2", 100, 5), 1033),
        PCB(Process("3", 240, 16), 1133),
        PCB(Process("4", 300, 22), 1373),
        PCB(Process("6", 375, 12), 1673)
    )

    private val expectedHoles = mapOf(
        PCB(Process("2", 100, 5), 1033) to 1,
        PCB(Process("1", 521, 10), 512) to 2,
        PCB(Process("6", 375, 12), 1673) to 3,
        PCB(Process("3", 240, 16), 1133) to 1,
        PCB(Process("4", 300, 22), 512) to 2 // when this is removed, a compaction occurs
    )

    private lateinit var readyQueue: ReadyQueue

    @BeforeEach
    fun setUp() {
        readyQueue = ReadyQueue()
    }

    @Test
    @DisplayName("ReadyQueue should be empty upon creation")
    fun testIsEmptyUponCreation() {
        assertTrue(readyQueue.isEmpty()) { "Queue should be empty upon creation" }
    }

    @Test
    @DisplayName("ReadyQueue should not be empty after adding processes")
    fun testIsNotEmptyAfterAddingProcesses() {
        readyQueue.addAll(processes)
        assertFalse(readyQueue.isEmpty()) { "Queue should not be empty after adding processes" }
    }

    @Test
    @DisplayName("ReadyQueue should correctly add individual processes")
    fun testAddIndividualProcesses() {
        processes.forEach { process ->
            assertTrue(readyQueue.add(process)) { "Process ${process.id} should be added successfully" }
        }
    }

    @Test
    @DisplayName("ReadyQueue addAll should add all processes")
    fun testAddAllProcesses() {
        readyQueue.addAll(processes)

        val expectedQueue = PriorityQueue<PCB>().apply { addAll(expectedResults) }
        assertEquals(expectedQueue.toList(), readyQueue.toList()) { "Processes should be added correctly" }
    }

    @Test
    @DisplayName("ReadyQueue should add processes in correct order")
    fun testProcessesAddedInCorrectOrder() {
        readyQueue.addAll(processes)
        assertEquals(
            expectedResults,
            readyQueue.recentProcesses().toList()
        ) { "Processes should be in the correct order" }

    }

    @Test
    @DisplayName("ReadyQueue should not add a process when full")
    fun testProcessNotAddedWhenFull() {
        val largeProcess = Process("7", readyQueue.maximumSize, 10)
        assertFalse(readyQueue.add(largeProcess)) { "Process should not be added when it exceeds queue size" }
    }

    @Test
    @DisplayName("ReadyQueue should not add a process that cannot fit")
    fun testProcessNotAddedWhenCannotFit() {
        val largeProcess = Process("7", readyQueue.maximumSize, 10)
        assertFalse(readyQueue.add(largeProcess)) { "Process should not be added when it cannot fit" }
    }

    @Test
    @DisplayName("ReadyQueue should add a process that exactly fits the remaining space")
    fun testAddProcessThatExactlyFitsRemainingSpace() {
        readyQueue.add(Process("5", readyQueue.remainingSize() - 15, 2))
        readyQueue.add(Process("6", 15, 10))
        readyQueue.remove()
        val exactFitProcess = Process("7", readyQueue.remainingSize(), 10)
        assertTrue(readyQueue.add(exactFitProcess)) { "Process should be added when it exactly fits the remaining space" }
    }


    @Test
    @DisplayName("ReadyQueue should manage space correctly when adding processes incrementally")
    fun testAddProcessesIncrementally() {
        val incrementalProcesses = listOf(
            Process("7", 100, 10),
            Process("8", 200, 10),
            Process("9", 300, 10)
        )

        incrementalProcesses.forEach { process ->
            assertTrue(readyQueue.add(process)) { "Process ${process.id} should be added successfully" }
        }
    }

    @Test
    fun testAddProcessThatHasLeftoverSpace() {
        readyQueue.add(Process("5", readyQueue.remainingSize() - 15, 2))
        readyQueue.add(Process("6", 15, 22))
        readyQueue.remove()
        val hasExcessSpace = Process("7", 100, 10)
        assertTrue(readyQueue.add(hasExcessSpace)) { "Process should be added when it exactly fits the remaining space" }
    }

    @Test
    @DisplayName("ReadyQueue remainingSize should return the correct remaining size")
    fun testRemainingSize() {
        readyQueue.addAll(processes)
        val expectedRemainingSize = readyQueue.maximumSize - processes.sumOf { it.size }
        assertEquals(expectedRemainingSize, readyQueue.remainingSize()) { "Remaining size should be correct" }
    }

    @Test
    @DisplayName("ReadyQueue size should return the correct number of processes")
    fun testSize() {
        readyQueue.addAll(processes)
        assertEquals(
            processes.size,
            readyQueue.size()
        ) { "Size should be the number of processes including OS_PCB" }
    }

    @Test
    @DisplayName("ReadyQueue should remove processes and add holes in their place")
    fun testRemove() {
        expectedHoles.forEach { (pcb, int) ->
            {
                readyQueue.add(pcb.process)
                readyQueue.remove()
                assertEquals(int, readyQueue.holesSize())
            }
        }

    }

    @Test
    @DisplayName("ReadyQueue should not remove when empty")
    fun testRemoveIfEmpty() {
        assertNull(readyQueue.remove())
    }

    @Test
    @DisplayName("ReadyQueue holesSize should return the correct number of holes")
    fun testHolesSize() {
        readyQueue.addAll(processes)
        readyQueue.remove()
        val expected = 1

        assertEquals(
            expected,
            readyQueue.holesSize()
        ) { "Holes size should be the number of holes $expected in the available holes" }

    }

    @Test
    @DisplayName("ReadyQueue should remove all processes except OS_PCB")
    fun testRemoveAll() {
        readyQueue.apply {
            addAll(processes)
            assertTrue(removeAll()) { "All processes should be removed" }
            assertEquals(
                1,
                size()
            ) { "All processes except OS_PCB should be removed" }
        }
    }

    @Test
    @DisplayName("ReadyQueue should not remove OS_PCB")
    fun testRemoveOSPCB() {
        readyQueue.apply {
            addAll(processes)
            removeAll()

            assertEquals(
                1,
                size()
            ) { "Size should be 1 which is just the OS_PCB" }
        }
    }

    @Test
    @DisplayName("ReadyQueue should compact holes")
    fun testCompaction() {
        readyQueue.apply {
            addAll(processes)
            removeAll()
            assertEquals(expectedNumberOfCompactions, numOfCompactions)
            assertEquals(expectedNumberOfHoleWhenAllRemoved, holesSize())
        }
    }

    @Test
    @DisplayName("ReadyQueue should have holes in correct order")
    fun testHoleAreInCorrectOrder() {
        readyQueue.apply {
            addAll(processes)
            while (!isEmpty()) {
                remove().let { pcb ->
                    val int = expectedHoles[pcb]!!
                    assertEquals(
                        int,
                        readyQueue.holesSize()
                    ) { "PCB $pcb when removed should have $int number of hole" }
                }
            }
        }

    }


    @Test
    @DisplayName("ReadyQueue should clear all processes")
    fun testClear() {
        readyQueue.apply {
            addAll(processes)
            clear()
            assertEquals(
                1,
                size()
            ) { "Size should be 1 which is just the OS_PCB" }
            val actual = listOf(OS_PCB)
            assertEquals(readyQueue.toList(), actual)
            assertEquals(readyQueue.recentProcesses(), actual)
            assertEquals(readyQueue.availableHoles(), emptyList())
            assertEquals(readyQueue.holesSize(), 0)
            assertEquals(readyQueue.remainingSize(), readyQueue.maximumSize)
            assertEquals(readyQueue.numOfCompactions, 0)
        }
    }

    @Test
    @DisplayName("ReadyQueue toString should not be the default toString")
    fun testToString() {
        assertNotEquals(
            readyQueue.toString(),
            "${readyQueue.javaClass.name}@${Integer.toHexString(readyQueue.hashCode())}"
        )
    }

    @Test
    @DisplayName("ReadyQueue should throw if queues have data in them")
    fun testConstructorThrowsIfQueuesHaveDataInThem() {
        val throwingQueue = PriorityQueue<PCB>().apply { addAll(expectedResults) }
        val throwingStack = LinkedHashSet<PCB>().apply { addAll(expectedResults) }
        val throwingAvailableHoles = TreeSet<Hole>().apply { add(Hole.ofSize(OS_PROCESS)) }

        assertThrows<IllegalStateException> { ReadyQueue(throwingQueue, throwingStack, throwingAvailableHoles) }
        assertThrows<IllegalStateException> { ReadyQueue(throwingQueue) }
        assertThrows<IllegalStateException> { ReadyQueue(recentProcesses = throwingStack) }
        assertThrows<IllegalStateException> { ReadyQueue(availableHoles = throwingAvailableHoles) }
    }

    @Test
    @DisplayName("ReadyQueue should not throw if queues are empty")
    fun testConstructorDoesNotThrowIfQueueIsEmpty() {
        assertDoesNotThrow { ReadyQueue(PriorityQueue<PCB>(), LinkedHashSet<PCB>(), TreeSet<Hole>()) }
        assertDoesNotThrow { ReadyQueue(PriorityQueue<PCB>()) }
        assertDoesNotThrow { ReadyQueue(recentProcesses = LinkedHashSet<PCB>()) }
        assertDoesNotThrow { ReadyQueue(availableHoles = TreeSet<Hole>()) }
    }


}

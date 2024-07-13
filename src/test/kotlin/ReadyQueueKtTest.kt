import com.jamhour.model.Hole
import com.jamhour.model.OS_PCB
import com.jamhour.model.OS_PROCESS
import com.jamhour.model.PCB
import com.jamhour.model.Process
import com.jamhour.process_management.ReadyQueue
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.assertThrows
import java.lang.IllegalStateException
import java.util.PriorityQueue
import java.util.TreeSet
import kotlin.test.Test

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
    @DisplayName("Verify ReadyQueue is empty upon creation")
    fun testIsEmptyUponCreation() {
        assertTrue(readyQueue.isEmpty()) { "ReadyQueue should be empty when initially created" }
    }

    @Test
    @DisplayName("Ensure ReadyQueue is not empty after adding processes")
    fun testIsNotEmptyAfterAddingProcesses() {
        readyQueue.addAll(processes)
        assertFalse(readyQueue.isEmpty()) { "ReadyQueue should contain processes after adding them" }
    }

    @Test
    @DisplayName("Verify ReadyQueue correctly adds individual processes")
    fun testAddIndividualProcesses() {
        processes.forEach { process ->
            assertTrue(readyQueue.add(process)) { "Process $process should be successfully added to the ReadyQueue" }
        }
    }

    @Test
    @DisplayName("Confirm ReadyQueue addAll method adds all processes correctly")
    fun testAddAllProcesses() {
        readyQueue.addAll(processes)

        val expectedQueue = PriorityQueue<PCB>().apply { addAll(expectedResults) }
        assertEquals(
            expectedQueue.toList(),
            readyQueue.toList()
        ) { "All processes should be added to the ReadyQueue in the correct order" }
    }

    @Test
    @DisplayName("Verify ReadyQueue adds processes in correct priority order")
    fun testProcessesAddedInCorrectOrder() {
        readyQueue.addAll(processes)
        assertEquals(
            expectedResults,
            readyQueue.recentProcesses().toList()
        ) { "Processes in ReadyQueue should be ordered correctly based on priority" }

    }

    @Test
    @DisplayName("Ensure ReadyQueue rejects a process when queue is full")
    fun testAddProcessToFullQueue() {
        val largeProcess = Process("7", readyQueue.maximumSize, 10)
        assertFalse(readyQueue.add(largeProcess)) { "ReadyQueue should reject a process that exceeds its maximum size" }
    }

    @Test
    @DisplayName("Confirm ReadyQueue rejects a process that cannot fit in available space")
    fun testAddProcessExceedingAvailableSpace() {
        val largeProcess = Process("7", readyQueue.maximumSize, 10)
        assertFalse(readyQueue.add(largeProcess)) { "ReadyQueue should reject a process that cannot fit in the available space" }
    }

    @Test
    @DisplayName("Verify ReadyQueue accepts a process that exactly fits remaining space")
    fun testAddProcessExactlyFittingRemainingSpace() {
        readyQueue.add(Process("5", readyQueue.remainingSize() - 15, 2))
        readyQueue.add(Process("6", 15, 10))
        readyQueue.remove()
        val exactFitProcess = Process("7", readyQueue.remainingSize(), 10)
        assertTrue(readyQueue.add(exactFitProcess)) { "ReadyQueue should accept a process that exactly fits the remaining space" }
    }


    @Test
    @DisplayName("Ensure ReadyQueue manages space correctly when adding processes incrementally")
    fun testAddProcessesIncrementally() {
        val incrementalProcesses = listOf(
            Process("7", 100, 10),
            Process("8", 200, 10),
            Process("9", 300, 10)
        )

        incrementalProcesses.forEach { process ->
            assertTrue(readyQueue.add(process)) { "ReadyQueue should successfully add process $process incrementally" }
        }
    }

    @Test
    @DisplayName("Verify ReadyQueue accepts a process with leftover space")
    fun testAddProcessWithLeftoverSpace() {
        readyQueue.add(Process("5", readyQueue.remainingSize() - 15, 2))
        readyQueue.add(Process("6", 15, 22))
        readyQueue.remove()
        val hasExcessSpace = Process("7", 100, 10)
        assertTrue(readyQueue.add(hasExcessSpace)) { "ReadyQueue should accept a process that fits within the remaining space, even with leftover space" }
    }

    @Test
    @DisplayName("Confirm ReadyQueue remainingSize returns correct value after adding processes")
    fun testRemainingSize() {
        readyQueue.addAll(processes)
        val expectedRemainingSize = readyQueue.maximumSize - processes.sumOf { it.size }
        assertEquals(
            expectedRemainingSize,
            readyQueue.remainingSize()
        ) { "ReadyQueue's remaining size should accurately reflect available space after adding processes" }
    }

    @Test
    @DisplayName("Verify ReadyQueue size returns correct number of processes including OS_PCB")
    fun testSize() {
        readyQueue.addAll(processes)
        assertEquals(
            processes.size,
            readyQueue.size()
        ) { "ReadyQueue size should equal the total number of processes, including OS_PCB" }
    }

    @Test
    @DisplayName("Ensure ReadyQueue removes processes and adds holes correctly")
    fun testRemove() {
        expectedHoles.forEach { (pcb, expectedSize) ->
            readyQueue.add(pcb.process)
            readyQueue.remove()
            assertEquals(
                expectedSize,
                readyQueue.holesSize()
            ) { "ReadyQueue should have $expectedSize holes after removing process ${pcb.process}" }
        }
    }

    @Test
    @DisplayName("Verify ReadyQueue returns null when removing from empty queue")
    fun testRemoveFromEmptyQueue() {
        assertNull(readyQueue.remove()) { "Removing from an empty ReadyQueue should return null" }
    }

    @Test
    @DisplayName("Confirm ReadyQueue holesSize returns correct number of holes after removal")
    fun testHolesSize() {
        readyQueue.addAll(processes)
        readyQueue.remove()
        assertEquals(1, readyQueue.holesSize()) { "ReadyQueue should have 1 hole after removing a single process" }
    }

    @Test
    @DisplayName("Verify ReadyQueue removes all processes except OS_PCB")
    fun testRemoveAll() {
        readyQueue.apply {
            addAll(processes)
            assertTrue(removeAll()) { "ReadyQueue should successfully remove all processes" }
            assertEquals(
                1,
                size()
            ) { "ReadyQueue should contain only OS_PCB after removing all processes" }
        }
    }

    @Test
    @DisplayName("Ensure ReadyQueue does not remove OS_PCB when removing all processes")
    fun testRemoveOSPCB() {
        readyQueue.apply {
            addAll(processes)
            removeAll()

            assertEquals(
                1,
                size()
            ) { "ReadyQueue should retain only OS_PCB after removing all other processes" }
        }
    }

    @Test
    @DisplayName("Verify ReadyQueue compacts holes correctly after removing all processes")
    fun testCompaction() {
        readyQueue.apply {
            addAll(processes)
            removeAll()
            assertEquals(
                expectedNumberOfCompactions,
                numOfCompactions
            ) { "ReadyQueue should perform the expected number of compactions" }
            assertEquals(expectedNumberOfHoleWhenAllRemoved, holesSize()) {
                "ReadyQueue should have the expected number of holes after compaction"
            }
        }
    }

    @Test
    @DisplayName("Confirm ReadyQueue maintains correct hole order after process removal")
    fun testHolesAreInCorrectOrder() {
        readyQueue.apply {
            addAll(processes)
            while (!isEmpty()) {
                remove().let { pcb ->
                    val int = expectedHoles[pcb]!!
                    assertEquals(
                        int,
                        readyQueue.holesSize()
                    ) { "ReadyQueue should have $int holes after removing process ${pcb!!.process}" }
                }
            }
        }
    }


    @Test
    @DisplayName("Verify ReadyQueue clear method removes all processes except OS_PCB")
    fun testClear() {
        readyQueue.apply {
            addAll(processes)
            clear()
            assertEquals(
                1,
                size()
            ) { "Size should be 1 which is just the OS_PCB" }
            val actual = listOf(OS_PCB)
            assertEquals(actual, readyQueue.toList()) { "ReadyQueue should contain only OS_PCB after clearing" }
            assertEquals(actual, readyQueue.recentProcesses()) {
                "ReadyQueue's process list should match the expected list after clearing"
            }
            assertEquals(
                emptyList<Hole>(),
                readyQueue.availableHoles()
            ) { "ReadyQueue should have no available holes after clearing" }
            assertEquals(0, readyQueue.holesSize()) { "ReadyQueue should have no available holes after clearing" }
            assertEquals(readyQueue.maximumSize, readyQueue.remainingSize()) {
                "ReadyQueue's remaining size should equal its maximum size after clearing"
            }
            assertEquals(
                0,
                readyQueue.numOfCompactions
            ) { "ReadyQueue's compaction count should be reset to 0 after clearing" }
        }
    }

    @Test
    @DisplayName("Ensure ReadyQueue toString method returns a custom string representation")
    fun testToString() {
        assertNotEquals(
            "${readyQueue.javaClass.name}@${Integer.toHexString(readyQueue.hashCode())}",
            readyQueue.toString()
        ) { "ReadyQueue's toString() method should return a custom string representation" }
    }

    @Test
    @DisplayName("Verify ReadyQueue constructor throws exception when initialized with non-empty queues")
    fun testConstructorThrowsWithNonEmptyQueues() {
        val throwingQueue = PriorityQueue<PCB>().apply { addAll(expectedResults) }
        val throwingStack = LinkedHashSet<PCB>().apply { addAll(expectedResults) }
        val throwingAvailableHoles = TreeSet<Hole>().apply { add(Hole.ofSize(OS_PROCESS)) }

        assertThrows<IllegalStateException>(
            "Constructor should throw IllegalStateException when all queues are non-empty"
        ) {
            ReadyQueue(throwingQueue, throwingStack, throwingAvailableHoles)
        }

        assertThrows<IllegalStateException>(
            "Constructor should throw IllegalStateException when queue is non-empty"
        ) {
            ReadyQueue(throwingQueue)
        }

        assertThrows<IllegalStateException>(
            "Constructor should throw IllegalStateException when recentProcesses is non-empty"
        ) {
            ReadyQueue(recentProcesses = throwingStack)
        }

        assertThrows<IllegalStateException>(
            "Constructor should throw IllegalStateException when availableHoles is non-empty"
        ) {
            ReadyQueue(availableHoles = throwingAvailableHoles)
        }
    }

    @Test
    @DisplayName("Confirm ReadyQueue constructor does not throw exception with empty queues")
    fun testConstructorWithEmptyQueues() {
        assertDoesNotThrow(
            { ReadyQueue(PriorityQueue<PCB>(), LinkedHashSet<PCB>(), TreeSet<Hole>()) },
            "Constructor should not throw exception when all queues are empty"
        )

        assertDoesNotThrow(
            { ReadyQueue(PriorityQueue<PCB>()) },
            "Constructor should not throw exception when only queue is provided and empty"
        )

        assertDoesNotThrow(
            { ReadyQueue(recentProcesses = LinkedHashSet<PCB>()) },
            "Constructor should not throw exception when only recentProcesses is provided and empty"
        )

        assertDoesNotThrow(
            { ReadyQueue(availableHoles = TreeSet<Hole>()) },
            "Constructor should not throw exception when only availableHoles is provided and empty"
        )
    }

    @Test
    @DisplayName("Ensure ReadyQueue addAll handles partial process addition correctly when space is insufficient")
    fun testAddMultipleProcessesInsufficientSpace() {
        readyQueue.apply {
            val elementsToAdd = listOf(
                Process("1", 521, 10),
                Process("2", 100, 5),
                Process("3", 240, 16),
                Process("4", 300, 22),
                Process("5", 512, 3),
                Process("6", 512, 12),
            )
            val allAdded = addAll(elementsToAdd)
            assertFalse(allAdded) { "ReadyQueue should indicate failure when not all processes can be added" }
            assertTrue(size() < elementsToAdd.size) { "ReadyQueue should contain some processes when not all processes can be added" }
        }
    }

    @Test
    @DisplayName("Ensure ReadyQueue currentSize property returns the correct value")
    fun testCurrentSize() {
        readyQueue.apply {
            addAll(processes)
            assertEquals(
                readyQueue.maximumSize,
                currentSize
            ) { "ReadyQueue's current size should equal its maximum size when all processes are added $processes" }
        }
    }

    @Test
    @DisplayName("Ensure ReadyQueue currentSize property updates when a process is removed")
    fun testCurrentSizeUpdatesWhenRemovingAProcess() {
        readyQueue.apply {
            val process = Process("1", 521, 10)
            add(process)
            assertEquals(
                process.size + OS_PROCESS.size,
                currentSize
            ) { "ReadyQueue's current size should be the sum of its ${process.size} and the OS process size ${OS_PROCESS.size}" }
            remove()
            assertEquals(
                OS_PROCESS.size,
                currentSize
            ) { "ReadyQueue's current size should be the OS process size ${OS_PROCESS.size} after removing the process $process" }
        }
    }

}

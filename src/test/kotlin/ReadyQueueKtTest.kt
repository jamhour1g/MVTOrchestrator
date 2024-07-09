import com.jamhour.ReadyQueue
import com.jamhour.model.OS_PCB
import com.jamhour.model.OS_PROCESS
import com.jamhour.model.PCB
import com.jamhour.model.Process
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import java.util.PriorityQueue
import kotlin.test.Test
import kotlin.test.fail

class ReadyQueueKtTest {

    private val processes = listOf(
        OS_PROCESS,
        Process("1", 521, 10),
        Process("2", 100, 5),
        Process("3", 240, 16),
        Process("4", 300, 22),
        Process("6", 375, 12)
    )

    private val expectedResults = listOf(
        OS_PCB,
        PCB(Process("1", 521, 10), 512),
        PCB(Process("2", 100, 5), 1033),
        PCB(Process("3", 240, 16), 1133),
        PCB(Process("4", 300, 22), 1373),
        PCB(Process("6", 375, 12), 1673)
    )

    private lateinit var readyQueue: ReadyQueue

    @BeforeEach
    fun setUp() {
        readyQueue = ReadyQueue()
    }

    @Test
    @DisplayName("ReadyQueue should be empty upon creation")
    fun isEmptyUponCreation() {
        assertTrue(readyQueue.isEmpty()) { "Queue should be empty upon creation" }
    }

    @Test
    @DisplayName("ReadyQueue addAll should add all processes")
    fun addAllProcesses() {
        readyQueue.addAll(processes)

        val expectedQueue = PriorityQueue<PCB>().apply { addAll(expectedResults) }
        assertEquals(expectedQueue.toList(), readyQueue.toList()) { "Processes should be added correctly" }
    }

    @Test
    @DisplayName("ReadyQueue should correctly add individual processes")
    fun addIndividualProcesses() {
        processes.forEach { process ->
            assertTrue(readyQueue.add(process)) { "Process ${process.id} should be added successfully" }
        }
    }

    @Test
    @DisplayName("ReadyQueue should not be empty after adding processes")
    fun isNotEmptyAfterAddingProcesses() {
        readyQueue.addAll(processes)
        assertFalse(readyQueue.isEmpty()) { "Queue should not be empty after adding processes" }
    }

    @Test
    @DisplayName("ReadyQueue should add processes in correct order")
    fun processesAddedInCorrectOrder() {
        readyQueue.addAll(processes)
        assertEquals(
            expectedResults,
            readyQueue.recentProcessesIterator().toList()
        ) { "Processes should be in the correct order" }

    }

    @Test
    @DisplayName("ReadyQueue should not add a process when full")
    fun processNotAddedWhenFull() {
        val largeProcess = Process("7", readyQueue.maximumSize, 10)
        assertFalse(readyQueue.add(largeProcess)) { "Process should not be added when it exceeds queue size" }
    }

    @Test
    @DisplayName("ReadyQueue should not add a process that cannot fit")
    fun processNotAddedWhenCannotFit() {
        val largeProcess = Process("7", readyQueue.maximumSize, 10)
        assertFalse(readyQueue.add(largeProcess)) { "Process should not be added when it cannot fit" }
    }

    @Test
    @DisplayName("ReadyQueue should add a process that exactly fits the remaining space")
    fun addProcessThatExactlyFitsRemainingSpace() {
        val exactFitProcess = Process("5", readyQueue.remainingSize(), 10)
        assertTrue(readyQueue.add(exactFitProcess)) { "Process should be added when it exactly fits the remaining space" }
    }


    @Test
    @DisplayName("ReadyQueue should manage space correctly when adding processes incrementally")
    fun addProcessesIncrementally() {
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
    @DisplayName("ReadyQueue holesSize should return the correct number of holes")
    fun testHolesSize() {
        fail("Test not implemented yet.") // TODO update this when the remove function is implemented
    }

}

import com.jamhour.model.Process
import com.jamhour.process_management.ReadyQueue
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import kotlin.test.Test

class ReadyQueueCompanionObjectKtTest {

    @Test
    @DisplayName("Build ReadyQueue with default shouldAddHole (true)")
    fun testBuildReadyQueueDefaultShouldAddHole() {
        val readyQueue = ReadyQueue.build {
            add(Process("1", 100, 10))
        }

        assertEquals(2, readyQueue.size()) { "ReadyQueue should contain OS_PCB and one process" }
        assertTrue(readyQueue.holesSize() > 0) { "ReadyQueue should have holes as shouldAddHole is true" }
    }

    @Test
    @DisplayName("Build ReadyQueue with shouldAddHole set to false")
    fun testBuildReadyQueueShouldAddHoleFalse() {
        val readyQueue = ReadyQueue.build(false) {
            add(Process("1", 100, 10))
        }

        assertEquals(2, readyQueue.size()) { "ReadyQueue should contain OS_PCB and one process" }
        assertEquals(0, readyQueue.holesSize()) { "ReadyQueue should have no holes as shouldAddHole is false" }
    }

    @Test
    @DisplayName("Build ReadyQueue with no processes and default shouldAddHole (true)")
    fun testBuildReadyQueueNoProcessesDefaultShouldAddHole() {
        val readyQueue = ReadyQueue.build {}

        assertEquals(1, readyQueue.size()) { "ReadyQueue should contain only OS_PCB" }
        assertTrue(readyQueue.holesSize() > 0) { "ReadyQueue should have holes as shouldAddHole is true" }
    }

    @Test
    @DisplayName("Build ReadyQueue with no processes and shouldAddHole set to false")
    fun testBuildReadyQueueNoProcessesShouldAddHoleFalse() {
        val readyQueue = ReadyQueue.build(shouldAddHole = false) {}

        assertEquals(1, readyQueue.size()) { "ReadyQueue should contain only OS_PCB" }
        assertEquals(0, readyQueue.holesSize()) { "ReadyQueue should have no holes as shouldAddHole is false" }
    }

    @Test
    @DisplayName("Build ReadyQueue with multiple processes and default shouldAddHole (true)")
    fun testBuildReadyQueueMultipleProcessesDefaultShouldAddHole() {
        val readyQueue = ReadyQueue.build {
            add(Process("1", 100, 10))
            add(Process("2", 200, 20))
        }

        assertEquals(3, readyQueue.size()) { "ReadyQueue should contain OS_PCB and two processes" }
        assertTrue(readyQueue.holesSize() > 0) { "ReadyQueue should have holes as shouldAddHole is true" }
    }

    @Test
    @DisplayName("Build ReadyQueue with multiple processes and shouldAddHole set to false")
    fun testBuildReadyQueueMultipleProcessesShouldAddHoleFalse() {
        val readyQueue = ReadyQueue.build(false) {
            add(Process("1", 100, 10))
            add(Process("2", 200, 20))
        }

        assertEquals(3, readyQueue.size()) { "ReadyQueue should contain OS_PCB and two processes" }
        assertEquals(0, readyQueue.holesSize()) { "ReadyQueue should have no holes as shouldAddHole is false" }
    }
}
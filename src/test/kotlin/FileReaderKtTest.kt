import com.jamhour.model.Process
import com.jamhour.util.linePattern
import com.jamhour.util.readFile
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import java.nio.file.Path
import kotlin.test.Test

class FileReaderKtTest {

    @Test
    @DisplayName("linePattern matches correctly formatted lines")
    fun testLinePatternMatchesValidLines() {
        val correctlyFormattedLines = listOf(
            "1 1024 5000",
            "2A 2048 10000",
            "3abs 4096 15000",
            "OS 16384 25000", // note this should not be present in the file
            "6  1024  5000 ",
            "7  2048  10000  ",
            "8  4096  15000",
            "9  8192  20000",
            "10  16384  25000",
            "yazan  16384  25000",
            "jamhour  16384  25000"
        )

        correctlyFormattedLines.forEach {
            assertTrue(linePattern.matcher(it).matches()) { "Line '$it' should match pattern: $linePattern" }
        }
    }

    @Test
    @DisplayName("linePattern rejects incorrectly formatted lines")
    fun testLinePatternRejectsInvalidLines() {
        val incorrectlyFormattedLines = listOf(
            "2 2048.1 10000",
            "3 4096 15000.1",
            " Jamhour 4096 15000.1",
            "4-12 8192 20000",
            "5 16384A 25000A",
            "67. 1024 5000",
            "7-2048-10000",
            "8_4096_15000",
            "9+8192+20000",
            " 254 123",
            "",
            " ",
            "101638425000"
        )

        incorrectlyFormattedLines.forEach {
            assertFalse(linePattern.matcher(it).matches()) { "Line '$it' should not match pattern: $linePattern" }
        }
    }

    @Test
    @DisplayName("readFile correctly parses a correctly formatted file")
    fun testReadFileCorrectlyParsesFormattedFile() = runTest {
        val fileName = "/jobs_correct_format.txt"
        val resource = javaClass.getResource(fileName)
            ?: fail("Resource $fileName not found")

        val processes = readFile(Path.of(resource.toURI()))
        val expectedResult = listOf(
            Process("1", 521, 10),
            Process("2", 100, 5),
            Process("3", 240, 16),
            Process("4", 300, 22),
            Process("5", 700, 3),
            Process("6", 890, 30),
            Process("7", 800, 10),
            Process("8", 900, 17),
            Process("9", 1200, 13),
            Process("10", 1000, 12),
            Process("11", 650, 9)
        )

        assertEquals(expectedResult, processes) { "Processes should match expected result $expectedResult" }
    }

    @Test
    @DisplayName("readFile returns empty list for incorrectly formatted file")
    fun testReadFileReturnsEmptyListForIncorrectlyFormattedFile() = runTest {
        val fileName = "/jobs_wrong_format.txt"
        val resource = javaClass.getResource(fileName)
            ?: fail { "Resource $fileName not found" }

        val processes = readFile(Path.of(resource.toURI()))

        assertTrue(processes.isEmpty()) { "Processes should be an empty list instead of $processes" }
    }
}

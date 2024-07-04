import com.jamhour.Process
import com.jamhour.linePattern
import com.jamhour.readFile
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import java.nio.file.Path
import kotlin.test.Test

class FileReaderKtTest {

    @Test
    @DisplayName("Verify line pattern correctly matches valid lines")
    fun linePatternMatchesValidLines() {
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

        assertTrue(
            correctlyFormattedLines.all { linePattern.matcher(it).matches() }
        )
        { "Line should match pattern: $linePattern" }

    }

    @Test
    @DisplayName("Verify line pattern rejects invalid lines")
    fun linePatternRejectsInvalidLines() {
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

        assertFalse(
            incorrectlyFormattedLines.all { linePattern.matcher(it).matches() }
        ) { "Line should not match pattern: $linePattern" }
    }

    @Test
    @DisplayName("Verify readFile correctly parses a correctly formatted file")
    fun readFileCorrectlyParsesFormattedFile() = runTest {
        val fileName = "/jobs_correct_format.txt"
        val resource = javaClass.getResource(fileName)
            ?: org.junit.jupiter.api.fail { "Resource $fileName not found" }

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

        assertTrue(processes == expectedResult) { "Processes should match expected result $expectedResult" }
    }

    @Test
    @DisplayName("Verify readFile returns empty list for incorrectly formatted file")
    fun readFileReturnsEmptyListForIncorrectlyFormattedFile() = runTest {
        val fileName = "/jobs_wrong_format.txt"
        val resource = javaClass.getResource(fileName)
            ?: org.junit.jupiter.api.fail { "Resource $fileName not found" }

        val processes = readFile(Path.of(resource.toURI()))

        assertTrue(processes.isEmpty()) { "Processes should be an empty list instead of $processes" }
    }
}

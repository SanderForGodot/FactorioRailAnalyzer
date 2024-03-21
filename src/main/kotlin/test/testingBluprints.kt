package test

import decodeBpStringFromFilename
import factorioRailAnalyzer
import setCLIOptions

fun main() {
    toTest.forEach {
        setCLIOptions(listOf("-i"))
        val jsonString = decodeBpStringFromFilename(it.key)
        val result = factorioRailAnalyzer(jsonString)
        if (result != it.value) {
            println("${it.key} has Wrong output, it should be ${it.value}, but is $result\n")
        } else {
            println("\n${it.key} passed!\n")

        }
    }
}

val toTest: MutableMap<String, Boolean> = mutableMapOf(
    "src/test.test/resources/testBPs/keisOhneDeadlock.txt" to true,
    "src/test.test/resources/testBPs/minimalDeadlock.txt" to true,
    "src/test.test/resources/testBPs/minimalNonDeadlock.txt" to false,
    "src/test.test/resources/testBPs/silpleCrosing.txt" to false,
    "src/test.test/resources/testBPs/thenotoriusYESdl.txt" to true,
    "src/test.test/resources/testBPs/keisMitDeadlock.txt" to true,
)
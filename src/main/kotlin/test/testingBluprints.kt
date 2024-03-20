fun main() {
    toTest.forEach {
        setCLIOptions(listOf("-i"))
        val jsonString = decodeBpStringFromFilename(it.key)
        val result = factorioRailAnalyzer(jsonString)
        if (result != it.value) {
            println("\n${it.key} has Wrong output, it should be ${it.value}, but is $result\n")
        }
    }
}

val toTest: MutableMap<String, Boolean> = mutableMapOf(
    "src/test/resources/testBPs/keisOhneDeadlock.txt" to false,
    "src/test/resources/testBPs/minimalDeadlock.txt" to true,
    "src/test/resources/testBPs/minimalNonDeadlock.txt" to false,
    "src/test/resources/testBPs/silpleCrosing.txt" to false,
    "src/test/resources/testBPs/thenotoriusYESdl.txt" to false,
    "src/test/resources/testBPs/keisMitDeadlock.txt" to true,
)
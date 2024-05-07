import FRA.factorioRailAnalyzer
import java.nio.file.Files
import java.nio.file.Path

fun main(args: Array<String>) {


    val options = args.filter { it.startsWith("-") }
    if (args.isEmpty()){
        showCLIHelp()
    }
    setCLIOptions(options)
    dbgPrintln("Program arguments: ${args.joinToString()}")
    val inputBlueprintString = args.filter { it.startsWith("0") }
    val blueprintFile = args.filter { !it.startsWith("-") }
    if (inputBlueprintString.size > 1) {
        println("Too Many Blueprints provided")
        return
    }/*else if (inputBlueprintString.size<1){ // Add for CLI Builds
        println("No Blueprint provided")
        return
    }*/

    val jsonString: String
    if (inputBlueprintString.size == 1) {
        jsonString = decodeBpString(inputBlueprintString.first())
    } else {
        if (blueprintFile.isNotEmpty()) {
            if (Files.exists(Path.of(blueprintFile.first()))) {
                jsonString = decodeBpStringFromFilename(blueprintFile.first())

            } else {
                println("File not Found")
                return
            }
        } else {
            if (Files.exists(Path.of("src/main/kotlin/exampleBP.txt"))) {
                jsonString = decodeBpStringFromFilename("src/main/kotlin/exampleBP.txt")//default
            } else {
                println("No Blueprint provided")
                return
            }
        }
    }
    factorioRailAnalyzer(jsonString)

}


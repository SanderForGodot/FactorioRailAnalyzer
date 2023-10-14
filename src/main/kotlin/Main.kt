import com.google.gson.Gson
import factorioBlueprint.Blueprint
import factorioBlueprint.ResultBP
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.io.IOException
import java.nio.file.Files.createFile
import java.nio.file.Path
import java.util.function.Consumer

fun main(args: Array<String>) {

    println("Program arguments: ${args.joinToString()}")


    val jsonString: String = File("bp.json").readText(Charsets.UTF_8)
    val gson = Gson()
    var resultBP = gson.fromJson(jsonString, ResultBP::class.java)
    println(resultBP)


}


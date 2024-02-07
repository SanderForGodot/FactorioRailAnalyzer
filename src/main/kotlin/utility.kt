import java.io.ByteArrayOutputStream
import java.io.File
import java.util.*
import java.util.zip.Inflater

//Made by @marcouberti on github
//Source: https://gist.github.com/marcouberti/40dbbd836562b35ace7fb2c627b0f34f
//some idea what this funtion does, but we didn't write it
fun ByteArray.zlibDecompress(): String {
    val inflater = Inflater()
    val outputStream = ByteArrayOutputStream()

    return outputStream.use {
        val buffer = ByteArray(1024)

        inflater.setInput(this)

        var count = -1
        while (count != 0) {
            count = inflater.inflate(buffer)
            outputStream.write(buffer, 0, count)
        }

        inflater.end()
        outputStream.toString("UTF-8")
    }
}

fun decodeBpSting(filename: String): String {
    val base64String: String = File(filename).readText(Charsets.UTF_8)
    val decoded = Base64.getDecoder().decode(base64String.substring(1))
    val str: String = decoded.zlibDecompress()
    println(str)
    return str
}



// addes an ellement to a list only if that item isn`t altreddy in the list
// true == added
// false == failed
fun <E> ArrayList<E>.addUnique(element: E): Boolean {
    if (!this.contains(element)) {
        this.add(element)
        return true
    }
    return false;
}

fun <E> ArrayList<E>.addUniqueWithDBG(element: E) {
    if (!this.addUnique(element)) {
        println("addUnique failed: array: " + this + "element: " + element)
    }
}

import factorioBlueprint.Position
import java.io.ByteArrayOutputStream
import java.io.File
import java.util.*
import java.util.zip.Inflater

//Made by @marcouberti on github
//Source: https://gist.github.com/marcouberti/40dbbd836562b35ace7fb2c627b0f34f
//some idea what this function does, but we didn't write it
//
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

fun decodeBpStringFromFilename(filename: String): String {
    val base64String: String = File(filename).readText(Charsets.UTF_8)
    val decoded = Base64.getDecoder().decode(base64String.substring(1))//TODO: Check for Empty String/File
    val str: String = decoded.zlibDecompress()
    dbgPrintln(str)
    return str
}

fun decodeBpString(blueprint: String): String {
    val base64String: String = blueprint
    val decoded = Base64.getDecoder().decode(base64String.substring(1))//TODO: Check for Empty String/File
    val str: String = decoded.zlibDecompress()
    dbgPrintln(str)
    return str
}


// adds an element to a list only if that item isn`t already in the list
// returns true if item was added
fun <E> ArrayList<E>.addUnique(element: E): Boolean {
    if (!this.contains(element)) {
        this.add(element)
        return true
    }
    return false
}


//reference from https://bryceboe.com/2006/10/23/line-segment-intersection-algorithm/
fun intersect(a: Position, b: Position, c: Position, d: Position): Boolean {
    if (a == c || a == d || b == c || b == d)
        return true
    return ccw(a, c, d) != ccw(b, c, d) && ccw(a, b, c) != ccw(a, b, d)
}

fun ccw(a: Position, b: Position, c: Position): Boolean {
    return (c.y - a.y) * (b.x - a.x) > (b.y - a.y) * (c.x - a.x)

}
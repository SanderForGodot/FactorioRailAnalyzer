import Clases.Block
import Clases.Edge
import Clases.EntityType
import Clases.Grafabel
import FRA.Graph
import factorioBlueprint.Entity
import factorioBlueprint.Position
import java.awt.Color
import java.awt.Desktop
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.text.MessageFormat
import java.time.LocalTime
import kotlin.math.roundToInt
import kotlin.random.Random.Default.nextInt

/* How to use:
have graphviz installed, possible by downloading the installer from here:
https://graphviz.org/download/
it will produce one output.svg per edge
 */


// Original Code copied from   https://stackoverflow.com/questions/25119877/graphviz-with-java

class Graphviz {
    val OPEN_GRAPH = "digraph G { \n"
    val NODE = "{0} [label=\"{1}\"]; \n"
    val NODEXY = "{0} [label=\"{1}\"\npos = \"{2},{3}!\"]; \n"
    val EDGE = "{0} -> {1}; \n"
    val EDGECOLOR = "{0} -> {1}[color=\"{2}\"]; \n"
    val CLOSE_GRAPH = "} \n"


    private fun appendEntity(sb: Appendable, entity: Entity) {
        val name =
            entity.entityType.name + " id:" + entity.entityNumber + " r:" + entity.direction + "pos:" + entity.position.x + ";" + entity.position.y
        sb.append(
            MessageFormat.format(
                NODEXY,
                entity.entityNumber,
                name,
                (entity.position.x * 2).toString(),
                (entity.position.y * 2).toString()
            )
        )
        entity.leftNextRail.forEach {
            sb.append(MessageFormat.format(EDGECOLOR, entity.entityNumber, it.entityNumber, "red"))
        }
        entity.rightNextRail.forEach {
            sb.append(MessageFormat.format(EDGECOLOR, entity.entityNumber, it.entityNumber, "blue"))
        }
        entity.signalOnTheLeft.forEach {
            sb.append(MessageFormat.format(EDGECOLOR, entity.entityNumber, it.entityNumber, "blue"))
        }
        entity.signalOnTheRight.forEach {
            sb.append(MessageFormat.format(EDGECOLOR, entity.entityNumber, it.entityNumber, "red"))
        }

    }

    fun generateEntityRelations(entityList: ArrayList<Entity>) {
        val stringBuilder = StringBuilder()
        stringBuilder.append(OPEN_GRAPH)
        entityList.forEach { entity ->
            //println(entity.relevantShit())
            appendEntity(stringBuilder, entity)
        }
        stringBuilder.append(CLOSE_GRAPH)
        buildFile(stringBuilder, "EntityRelations")
    }


}

fun <T : Grafabel> ArrayList<T>.visualize(
    fileName: String,
    graph: Graph<T>,
    fn: (T) -> ArrayList<T>?,
): ArrayList<T> {
    val stringBuilder = StringBuilder()
    val g = Graphviz()
    stringBuilder.append(g.OPEN_GRAPH)
    val masterList = arrayListOf<String>()
    val haveDoneIt = ArrayList<Int>()

    for (item in this) {
        if (fn.invoke(item)?.size == 0) continue
        val pos = item.pos().div(5).round()
        if (!haveDoneIt.contains(item.uniqueID())) {
            haveDoneIt.add(item.uniqueID())
            stringBuilder.append(MessageFormat.format(g.NODEXY, item.uniqueID(), item.uniqueID(), pos.x, pos.y * -1))
        }
        fn.invoke(item)?.forEach {

            masterList.addUnique(
                MessageFormat.format(g.EDGE, item.uniqueID(), it.uniqueID())
            )
        }
    }
    masterList.forEach {
        stringBuilder.append(it)
    }

    graph.circularDependencies.forEach { dl ->
        val color = nextColor()

        for (i in dl.indices)
            stringBuilder.append(
                MessageFormat.format(
                    g.EDGECOLOR,
                    dl[i].uniqueID(),
                    dl[(i + 1) % dl.size].uniqueID(),
                    color.toHex()
                )
            )
    }
    stringBuilder.append(g.CLOSE_GRAPH)
    buildFile(stringBuilder, fileName)
    return this
}


fun <T : Grafabel> ArrayList<T>.visualizeWithRef(
    fileName: String,

    fn: (T) -> ArrayList<T>?,
    ref: (T) -> Grafabel?
): ArrayList<T> {
    val stringBuilder = StringBuilder()
    val g = Graphviz()
    stringBuilder.append(g.OPEN_GRAPH)
    val masterList = arrayListOf<String>()

    val relevant = ArrayList<Grafabel>()
    for (item in this) {
        val node = ref.invoke(item)!!
        if (fn.invoke(item) == null) continue
        if (fn.invoke(item)!!.size == 0) continue
        fn.invoke(item)!!.forEach {
            relevant.addUnique(node)
            relevant.addUnique(ref.invoke(it)!!)
            masterList.addUnique(
                MessageFormat.format(g.EDGE, node.uniqueID(), ref.invoke(it)!!.uniqueID())
            )
        }
    }
    relevant.forEach { node ->
        val pos = node.pos().div(5).round()
        dbgPrintln("${node.uniqueID()}:$pos")
        stringBuilder.append(MessageFormat.format(g.NODEXY, node.uniqueID(), node.label(), pos.x, pos.y * -1))
    }


    masterList.forEach {
        stringBuilder.append(it)
    }
    stringBuilder.append(g.CLOSE_GRAPH)
    buildFile(stringBuilder, fileName)
    return this
}



fun Color.toHex(): String {

    return String.format("#%02x%02x%02x", this.red, this.green, this.blue)
}


fun buildFile(stringBuilder: StringBuilder, fileName: String) {
    try {
        // for png: val result = ProcessBuilder("dot", "-Kfdp", "-n", "-Tpng", "input.dot", "-o $fileName.png")
        // for svg: val result = ProcessBuilder("dot", "-Kfdp", "-n", "-Tsvg", "input.dot", "-o $fileName.svg")
        if (!Files.exists(Path.of("GraphvizInput"))) {
            Files.createDirectory(Path.of("GraphvizInput"))
        }
        if (!Files.exists(Path.of("GraphvizOutput"))) {
            Files.createDirectory(Path.of("GraphvizOutput"))
        }
        File("GraphvizInput/$fileName.dot").writeText(stringBuilder.toString())
        File("GraphvizOutput/input.dot").writeText(stringBuilder.toString())
        val result = ProcessBuilder(
            "dot",
            "-Glabel=Generated@${LocalTime.now()}",
            "-Kfdp",
            "-y",
            "-n",
            "-Gstart=250 ",
            "-Tsvg",

            //"gvpr -c \"N[\$.degree==0]{delete(NULL, \$)}\" GraphvizInput/$fileName.dot",
            "GraphvizInput/$fileName.dot",
            "-oGraphvizOutput/$fileName.svg"
        )
            .redirectOutput(ProcessBuilder.Redirect.INHERIT)
            .redirectError(ProcessBuilder.Redirect.INHERIT)
            .start()
            .waitFor()
    } catch (e: Exception) {
        //println(e)
        println("Graphviz is not installed, no output pictures generated\n Download at: https://graphviz.org/download/")
    }

}



import factorioBlueprint.Entity
import factorioBlueprint.Position
import graph.Graf
import java.awt.Color
import java.awt.Desktop
import java.io.File
import java.io.IOException
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


    //StringBuilder and genOutput are optional, so that this function can be used for printing the edge independently and
    //it can be used for printBlocks
    @Throws(IOException::class)
    fun printEdge(edge: Edge, j: Int, sb: StringBuilder = StringBuilder(), genOutput: Boolean = true) {
        if (genOutput) {
            sb.append(OPEN_GRAPH)
        }
        var i = 0
        while (i < edge.entityList.size - 1) {
            sb.append(
                MessageFormat.format(
                    NODEXY,
                    edge.entityList[i].entityNumber,
                    edge.entityList[i].entityType.name + " id:" + edge.entityList[i].entityNumber + " dir:" + edge.entityList[i].direction,
                    edge.entityList[i].position.x,
                    edge.entityList[i].position.y
                )
            )
            sb.append(MessageFormat.format(EDGE, edge.entityList[i].entityNumber, edge.entityList[i + 1].entityNumber))
            i++
        }
        if (genOutput) {
            sb.append(CLOSE_GRAPH)
            buildFile(sb, "Edge$j")
        }
    }

    fun appendEntity(sb: Appendable, entity: Entity) {
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

    fun printGraph(graph: MutableMap<Int, MutableList<Int>>) {
        val stringBuilder = StringBuilder()
        stringBuilder.append(OPEN_GRAPH)
        graph.forEach { from ->
            //point from entry.key to each entry.value
            stringBuilder.append(MessageFormat.format(NODE, from.key, from.key))
            from.value.forEach { pointTo ->
                stringBuilder.append(MessageFormat.format(EDGE, from.key, pointTo))
            }
        }
        stringBuilder.append(CLOSE_GRAPH)
        buildFile(stringBuilder, "graph")
    }

    fun printBlocksFromEdgeRelations(edgeList: ArrayList<Edge>) {
        val stringBuilder = StringBuilder()
        stringBuilder.append(OPEN_GRAPH)
        edgeList.forEach { edge ->
            var from = edge.belongsToBlock!!.id
            var pos = edge.belongsToBlock!!.calculateCenter()

            edge.wasIchBeobachte.forEach { innerEdge ->
                var to = innerEdge.belongsToBlock!!.id

                stringBuilder.append(
                    MessageFormat.format(
                        EDGE, from, to
                    )
                )
            }
            if (pos.x.isNaN())
                pos = edge.entityList[1].position
            var name = from.toString() + " | " + edge.entityList[1].position
            stringBuilder.append(
                MessageFormat.format(
                    NODE, from, name //, pos.x, pos.y
                )
            )


        }
        stringBuilder.append(CLOSE_GRAPH)
        buildFile(stringBuilder, "SandersEdges")

    }

    fun printBlocks(blockList: ArrayList<Block>) {
        val stringBuilder = StringBuilder()
        stringBuilder.append(OPEN_GRAPH)

        blockList.forEach { block ->
            val pos = block.calculateCenter()
            stringBuilder.append(
                MessageFormat.format(
                    NODE,
                    block.id,
                    "Block id:" + block.id + " \nEL:" + block.edgeListSting()
                )
            )
            block.dependingOn?.forEach { block2 ->
                stringBuilder.append(MessageFormat.format(EDGE, block2.id, block.id))
            }
        }
        stringBuilder.append(CLOSE_GRAPH)
        buildFile(stringBuilder, "Blocks")
    }

    fun printDeadlocks(Deadlock: Set<Block>, j: Int, sb: StringBuilder = StringBuilder(), genOutput: Boolean = true) {
        if (genOutput) {
            sb.append(OPEN_GRAPH)
        }
        var i = 0

        while (i < Deadlock.size - 1) {
            sb.append(
                MessageFormat.format(
                    NODE,
                    Deadlock.elementAt(i).id,
                    "Block id:" + Deadlock.elementAt(i).id + " \nEL:" + Deadlock.elementAt(i).edgeListSting()
                )
            )
            sb.append(MessageFormat.format(EDGE, Deadlock.elementAt(i).id, Deadlock.elementAt(i + 1).id))
            i++
        }
        if (genOutput) {
            sb.append(CLOSE_GRAPH)
            buildFile(sb, "Deadlock$j")
        }
    }


}

fun <T : Grafabel> ArrayList<T>.visualize(
    fileName: String,
    graph: Graf<T>,
    fn: (T) -> ArrayList<T>?,
): ArrayList<T> {
    val stringBuilder = StringBuilder()
    val g = Graphviz()
    stringBuilder.append(g.OPEN_GRAPH)
    var masterList = arrayListOf<String>()
    val haveDoneIt = ArrayList<Int>()

    for (item in this) {
        if (fn.invoke(item)?.size == 0) continue ?: continue
        var pos = item.pos().div(5).rounded()
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

    graph.circularDependencies.forEach {dl->
        val color :Color = Color(nextInt(255),nextInt(255),nextInt(255))

        for (i in dl.indices )
            stringBuilder.append(MessageFormat.format(g.EDGECOLOR,dl[i],dl[(i+1)%dl.size],color))
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
    var masterList = arrayListOf<String>()

    var relevant = ArrayList<Grafabel>()
    for (item in this) {
        var node = ref.invoke(item)!!
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
        val pos = node.pos().div(5).rounded()
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


fun svgFromPoints(size: Position, edgeList: ArrayList<Edge>, signalList: List<Entity>, pos: Position) {
    val stringBuilder = StringBuilder()
    stringBuilder.append(
        "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n" +
                "<!DOCTYPE svg PUBLIC \"-//W3C//DTD SVG 1.1//EN\"\n" +
                " \"http://www.w3.org/Graphics/SVG/1.1/DTD/svg11.dtd\">\n" +
                "<!-- Generated by sanders function\n" +
                " -->\n" +
                "<!-- Title: G Pages: 1 -->\n" +
                "<svg width=\"${size.x + 1}\" height=\"${size.y + 1}\" viewBox=\"-5 -5 ${size.x + 5} ${size.y + 5}\" xmlns=\"http://www.w3.org/2000/svg\" xmlns:xlink=\"http://www.w3.org/1999/xlink\">\n" +
                "<title>blueprinting image</title>\n"
    )
    stringBuilder.append("<rect width=\"${size.x + 10}\" height=\"${size.y + 10}\" x=\"-5\" y=\"-5\" fill=\"#8e8e8e\" />\n")

// schienen
    var listOfColors: List<Color> = listOf(
        Color.black,
        // Color.red,
        Color.pink,
        Color(255, 125, 0),
        Color.yellow,
        // Color.green,
        Color.magenta,
        // Color.cyan,
        Color.blue,
    )
    var bagRandom: MutableList<Color> = listOfColors.toMutableList()
    var dictionary: MutableMap<Int, Color> = mutableMapOf()

    listOfColors[0]

    edgeList.forEach { edge ->
        if (edge.collisionShape.size == 0) return@forEach
        var m = edge.collisionShape.joinToString { pos ->
            pos.x.roundToInt().toString() + "," + pos.y.roundToInt()
        }
        //color
        var color: Color = Color.BLACK
        //region random color
        if (CLIOptions[CLIFlags.EdgesGetRandomColor]!!) {
            if (edge.belongsToBlock != null) {
                if (dictionary.containsKey(edge.belongsToBlock!!.id)) {
                    color = dictionary[edge.belongsToBlock!!.id]!!
                } else {
                    if (bagRandom.isEmpty())
                        bagRandom = listOfColors.toMutableList()
                    var newC = bagRandom.random()
                    bagRandom.remove(newC)
                    dictionary[edge.belongsToBlock!!.id] = newC
                    color = newC
                }
            }
        }
        //endregion
        if (edge.rarwIchBinGefärlich)
            color = Color.RED

        stringBuilder.append("<path fill=\"none\" stroke=\"${color.toHex()}\" d=\"M$m\"/>\n")
        val b = edge.belongsToBlock
        if (b != null)
            stringBuilder.append("<text x=\"${b.pos().x}\" y=\"${b.pos().y}\"  font-size=\"5\" fill=\"green\">${b.id} </text>")
    }
    signalList.forEach { sig ->
        var color = if (sig.entityType == EntityType.Signal) Color.green else Color.cyan

        stringBuilder.append("<circle r=\"0.5\" cx=\"${sig.position.x}\" cy=\"${sig.position.y}\" fill=\"${color.toHex()}\" />\n")
    }
    stringBuilder.append("<circle r=\"2\" cx=\"${pos.x}\" cy=\"${pos.y}\"  fill=\"none\"  stroke=\"${Color.red.toHex()}\" stroke-width=\"0.3\" />\n")

    stringBuilder.append("</svg>")
    if (!Files.exists(Path.of("GraphvizOutput"))) {
        Files.createDirectory(Path.of("GraphvizOutput"))
    }
    File("GraphvizOutput/sandersSpecial.svg").writeText(stringBuilder.toString())
    if (CLIOptions[CLIFlags.InstantShowOutput]!!) {
        val dt = Desktop.getDesktop()
        dt.open(File("GraphvizOutput/sandersSpecial.svg"))
    }
}

private fun Color.toHex(): String {

    return String.format("#%02x%02x%02x", this.red, this.green, this.blue);
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
        println(e)
        //println("Graphviz is not installed, no output pictures generated")
        //todo: add console output
    }

}



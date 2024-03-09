import factorioBlueprint.Entity
import java.io.File
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.text.MessageFormat
import java.time.LocalTime

/* How to use:
have graphviz installed, possible by downloading the installer from here:
https://graphviz.org/download/
it will produce one output.svg per edge
 */


// Original Code copied from   https://stackoverflow.com/questions/25119877/graphviz-with-java

class Graphviz {
    private val OPEN_GRAPH = "digraph G { \n"
    private val NODE = "{0} [label=\"{1}\"]; \n"
    private val NODEXY = "{0} [label=\"{1}\"\npos = \"{2},{3}!\"]; \n"
    private val EDGE = "{0} -> {1}; \n"
    private val EDGECOLOR = "{0} -> {1}[color=\"{2}\"]; \n"
    private val CLOSE_GRAPH = "} \n"


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
            println(entity.relevantShit())
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
            var name = from.toString() +" | "+ edge.entityList[1].position
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
}


fun svgFromPoints(edgeList: ArrayList<Edge>)
{
    val stringBuilder = StringBuilder()
    stringBuilder.append(
        "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n" +
                "<!DOCTYPE svg PUBLIC \"-//W3C//DTD SVG 1.1//EN\"\n" +
                " \"http://www.w3.org/Graphics/SVG/1.1/DTD/svg11.dtd\">\n" +
                "<!-- Generated by sanders function\n" +
                " -->\n" +
                "<!-- Title: G Pages: 1 -->\n" +
                "<svg width=\"5000\" height=\"5000\" xmlns=\"http://www.w3.org/2000/svg\" xmlns:xlink=\"http://www.w3.org/1999/xlink\">\n" +
                "<title>G</title>\n"
    )
var total=edgeList.size
    var cunt =0

    edgeList.forEach{edge ->
        cunt++
        if (cunt%1000== 0) println("$cunt of$total")
        if(edge.collisionShape.size==0) return@forEach
        var m = edge.collisionShape.joinToString {pos->
            pos.x.toString() + "," + pos.y
        }
        stringBuilder.append( "<path fill=\"none\" stroke=\"black\" d=\"M$m\"/>\n")
    }

    stringBuilder.append("</svg>")
    File("GraphvizOutput/sandersSpecial.svg").writeText(stringBuilder.toString())


}


fun buildFile(stringBuilder: StringBuilder, fileName: String) {
    try {
        // for png: val result = ProcessBuilder("dot", "-Kfdp", "-n", "-Tpng", "input.dot", "-o $fileName.png")
        // for svg: val result = ProcessBuilder("dot", "-Kfdp", "-n", "-Tsvg", "input.dot", "-o $fileName.svg")
        //Files.createDirectory(Path.of("GraphvizOutput"))
        File("GraphvizInput/$fileName.dot").writeText(stringBuilder.toString())
        val result = ProcessBuilder(
            "dot",
            "-Glabel=Generated@${LocalTime.now()}",
            "-Kfdp",
           // "-y",
            "-n",
            "-Tsvg",
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
import factorioBlueprint.Entity
import java.io.File
import java.io.IOException
import java.text.MessageFormat


class Graphviz {
    private val OPEN_GRAPH = "digraph G { \n"
    private val NODE = "{0} [label=\"{1}\"]; \n"
    private val NODE2 = "{0} [label=\"{1}\"\npos = \"{2},{3}!\"]; \n"
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
                    NODE2,
                    edge.entityList[i].entityNumber,
                    edge.entityList[i].entityType.name + " id:"+edge.entityList[i].entityNumber+" dir:" + edge.entityList[i].direction,
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
        val name = entity.entityType.name +  " id:" + entity.entityNumber+" r:" + entity.direction +"pos:"+entity.position.x+";" +entity.position.y
        sb.append(
            MessageFormat.format(
                NODE2,
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

    fun printBlocks(blockList: ArrayList<Block>) {
        val stringBuilder = StringBuilder()
        stringBuilder.append(OPEN_GRAPH)

        blockList.forEach { block ->
            val pos = block.calculateCenter()
            stringBuilder.append(
                MessageFormat.format(
                    NODE,
                    block.id,
                    "Block id:" + block.id + " EdgeCount:" + block.edgeList.size
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


fun buildFile(stringBuilder: StringBuilder, fileName: String) {
    try {
        // for png: val result = ProcessBuilder("dot", "-Kfdp", "-n", "-Tpng", "input.dot", "-o $fileName.png")
        // for svg: val result = ProcessBuilder("dot", "-Kfdp", "-n", "-Tsvg", "input.dot", "-o $fileName.svg")
        File("input.dot").writeText(stringBuilder.toString())
        val result = ProcessBuilder("dot", "-Kfdp", "-y", "-n", "-Tsvg", "input.dot", "-o $fileName.svg")
            .redirectOutput(ProcessBuilder.Redirect.INHERIT)
            .redirectError(ProcessBuilder.Redirect.INHERIT)
            .start()
            .waitFor()
    } catch (e: Exception) {
        println("Graphviz is not installed, no output pictures generated")
        //todo: add console output
    }
}


/* How to use:
have graphviz installed, possible by downloading the installer from here:
https://graphviz.org/download/
it will produce one output.svg per edge
 */


// Original Code copied from   https://stackoverflow.com/questions/25119877/graphviz-with-java

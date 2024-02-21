import factorioBlueprint.Entity
import graph.Graph
import java.io.File
import java.io.IOException
import java.text.MessageFormat


class Graphviz {
    val OPEN_GRAPH = "digraph G { \n"
    val NODE = "{0} [label=\"{1}\"]; \n"
    val NODE2 = "{0} [label=\"{1}\"\npos = \"{2},{3}!\"]; \n"
    val EDGE = "{0} -> {1}; \n"
    val CLOSE_GRAPH = "} \n"

    var sb = StringBuilder()

    @Throws(IOException::class)
    fun format(sb: Appendable, edge: Edge) {
        sb.append(OPEN_GRAPH)
        var i = 0
        while (i < edge.entityList.size - 1) {
            sb.append(
                MessageFormat.format(
                    NODE2,
                    edge.entityList[i].entityNumber,
                    edge.entityList[i].entityType.name + edge.entityList[i].direction ,
                    edge.entityList[i].position.x,
                    edge.entityList[i].position.y
                )
            )
            sb.append(MessageFormat.format(EDGE, edge.entityList[i].entityNumber, edge.entityList[i + 1].entityNumber))
            i++
        }

        sb.append(CLOSE_GRAPH)
    }

    fun appendEntity(entity: Entity) {
        val name = entity.entityType.name + " r:" + entity.direction + " id:" + entity.entityNumber
        sb.append(MessageFormat.format(NODE2, entity.entityNumber, name,entity.position.x,entity.position.y))
        entity.leftNextRail.forEach {
            sb.append(MessageFormat.format(EDGE, entity.entityNumber, it.entityNumber))
        }
        entity.rightNextRail.forEach {
            sb.append(MessageFormat.format(EDGE, entity.entityNumber, it.entityNumber))
        }
        entity.signalOnTheLeft.forEach {
            sb.append(MessageFormat.format(EDGE, entity.entityNumber, it.entityNumber))
        }
        entity.signalOnTheRight.forEach {
            sb.append(MessageFormat.format(EDGE, entity.entityNumber, it.entityNumber))
        }

    }

    fun startGraph() {// gives graphviz syntax error if not called at the start of the graph
        sb.append(OPEN_GRAPH)
    }

    fun endGraph() {// gives graphviz syntax error if not called at the end of the graph
        sb.append(CLOSE_GRAPH)
    }

    fun createoutput() {// creates the output.svg file of the graph and clears the stringbuilder, so that a new graph can be started
       buildFile(sb, "output")
        println("Graphviz output created")
        sb = StringBuilder()
    }

    fun generateEntityRelations(entityList: ArrayList<Entity>) {
        startGraph()
        entityList.forEach { entity ->
            println(entity.relevantShit())
            appendEntity(entity)
        }
        endGraph()
        createoutput()
    }
}

fun printEdge(edge: Edge, i: Int) {
    val graphviz = Graphviz()
    val stringBuilder = StringBuilder()

    graphviz.format(stringBuilder, edge)
    //println(stringBuilder.toString())
    buildFile(stringBuilder, "output$i")

}

fun printGraf(graph: MutableMap<Int, MutableList<Int>>) {
    val graphviz = Graphviz()
    val stringBuilder = StringBuilder()
    stringBuilder.append(graphviz.OPEN_GRAPH)
    graph.forEach { from ->
        //point from entry.key to each entry.value

        stringBuilder.append(MessageFormat.format(graphviz.NODE, from.key, from.key))
        from.value.forEach { pointTo ->
            stringBuilder.append(MessageFormat.format(graphviz.EDGE, from.key, pointTo))
        }
    }
    stringBuilder.append(graphviz.CLOSE_GRAPH)

    //println(stringBuilder.toString())
    buildFile(stringBuilder, "graf")
}

fun buildFile(stringBuilder: StringBuilder, fileName: String) {
    File("input.dot").writeText(stringBuilder.toString())
    val result = ProcessBuilder("dot", "-Kfdp","-n","-Tsvg", "input.dot", "-o $fileName.svg")
        .redirectOutput(ProcessBuilder.Redirect.INHERIT)
        .redirectError(ProcessBuilder.Redirect.INHERIT)
        .start()
        .waitFor()
}


/* How to use:
have graphviz installed, possible by downloading the installer from here:
https://graphviz.org/download/
it will produce one output.svg per edge
 */


/* Original Coe copied from   https://stackoverflow.com/questions/25119877/graphviz-with-java
//Render nodes
for (node in edge.EntityList) {
    sb.append(MessageFormat.format(NODE, node.entityNumber, node.getName()))
    //Render edges for node
    for (targetEdge in node.getEdges()) {
        sb.append(MessageFormat.format(EDGE, node.entityNumber, targetEdge))
    }
}
sb.append(CLOSE_GRAPH)
}*/
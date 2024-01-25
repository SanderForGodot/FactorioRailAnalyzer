import factorioBlueprint.Entity
import java.io.File
import java.io.IOException
import java.text.MessageFormat


class Graphviz {
    private val OPEN_GRAPH = "digraph G { \n"
    private val NODE = "{0} [label=\"{1}\"]; \n"
    private val EDGE = "{0} -> {1}; \n"
    private val CLOSE_GRAPH = "} \n"

    var sb = StringBuilder()

    @Throws(IOException::class)
    fun format(sb: Appendable,edge: Edge) {
        sb.append(OPEN_GRAPH)
        var i=0
        while (i<edge.EntityList.size-1){
            sb.append(MessageFormat.format(NODE, edge.EntityList[i].entityNumber, edge.EntityList[i].name+edge.EntityList[i].direction ))
            sb.append(MessageFormat.format(EDGE, edge.EntityList[i].entityNumber, edge.EntityList[i+1].entityNumber ))
            i++
        }

        sb.append(CLOSE_GRAPH)
    }

    fun appendEntity(entity: Entity){
        val name = entity.name+entity.direction
        sb.append(MessageFormat.format(NODE, entity.entityNumber, name))
        entity.leftNextRail.forEach {
            sb.append(MessageFormat.format(EDGE, entity.entityNumber, it.entityNumber ))
        }
        entity.rightNextRail.forEach {
            sb.append(MessageFormat.format(EDGE, entity.entityNumber, it.entityNumber ))
        }
        entity.signalOntheLeft.forEach {
            sb.append(MessageFormat.format(EDGE, entity.entityNumber, it.entityNumber ))
        }
        entity.signalOntheRight.forEach {
            sb.append(MessageFormat.format(EDGE, entity.entityNumber, it.entityNumber ))
        }

    }
    fun startGraph(){// gives graphviz syntax error if not called at the start of the graph
        sb.append(OPEN_GRAPH)
    }

    fun endGraph(){// gives graphviz syntax error if not called at the end of the graph
        sb.append(CLOSE_GRAPH)
    }

    fun createoutput(){// creates the output.svg file of the graph and clears the stringbuilder, so that a new graph can be started
        File("input.dot").writeText(sb.toString())
        val result = ProcessBuilder("dot","-Tsvg","input.dot", "-o output.svg")
            .redirectOutput(ProcessBuilder.Redirect.INHERIT)
            .redirectError(ProcessBuilder.Redirect.INHERIT)
            .start()
            .waitFor()
        println("Graphviz output created")
        sb = StringBuilder()
    }
}

fun printEdge(edge: Edge, i: Int) {
    val graphviz = Graphviz()
    val stringBuilder = StringBuilder()

    graphviz.format(stringBuilder, edge)
    //println(stringBuilder.toString())
    File("input.dot").writeText(stringBuilder.toString())
    val result = ProcessBuilder("dot", "-Tsvg", "input.dot", "-o output$i.svg")
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
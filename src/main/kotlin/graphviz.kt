import java.io.IOException
import java.text.MessageFormat


class Graphviz {
    private val OPEN_GRAPH = "digraph G { \n"
    private val NODE = "{0} [label='{1}']; \n"
    private val EDGE = "{0} -> {1}; \n"
    private val CLOSE_GRAPH = "} \n"

    @Throws(IOException::class)
    fun format(sb: Appendable,edge: Edge) {
        sb.append(OPEN_GRAPH)
        var i=0
        while (i<edge.EntityList.size-1){
            sb.append(MessageFormat.format(EDGE, edge.EntityList[i].entityNumber, edge.EntityList[i+1].entityNumber ))
            i++
        }

        sb.append(CLOSE_GRAPH)
    }
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
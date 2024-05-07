package FRA

import Clases.Grafabel
import java.util.*


fun <T : Grafabel> ArrayList<T>.tiernan(fn: (T) -> Iterable<T>): Graph<T> {


    val g = Graph<T>()
    this.sorted()
    this.forEach {
        it.expandPath(g, fn)
    }
    return g.unionCircuits().reduceBasic()

}

fun <T : Grafabel> T.expandPath(graph: Graph<T>, fn: (T) -> Iterable<T>?) {
    graph.path.add(this)

    fn.invoke(this)?.forEach { neighbor ->
        /*1. The extension vertex cannot be in P.*/
        if (!graph.path.contains(neighbor)
            /*2. The extension vertex value must be larger than that of the first vertex of P.*/
            && (neighbor.uniqueID() > graph.path.first().uniqueID())
            /*3. The extension vertex cannot be closed to the last vertex in P.
                 H contains the list of vertices closed to each vertex*/
            && graph.visited[this]?.contains(neighbor) != true
        ) {
            neighbor.expandPath(graph, fn)
            graph.visited(this, neighbor)
        } else {
            if (graph.path.first() == neighbor) {
                graph.addPath()
            }
        }
    }
    graph.visited.remove(this)
    graph.path.removeLast()
}

class Graph<T : Grafabel> {
    val path: MutableList<T> = ArrayList()
    var visited: MutableMap<T, MutableList<T>> = mutableMapOf()
    var circularDependencies: MutableList<List<T>> = mutableListOf()
    fun visited(node: T, neighbor: T) {
        if (visited[node] == null) {
            visited[node] = mutableListOf(neighbor)
        } else {
            visited[node]!!.add(neighbor)
        }
    }

    fun addPath() {
        circularDependencies.add(path.toSet().toList())
    }

    fun unionCircuits(): Graph<T> {
        val dl = circularDependencies
        for (x in 0 until dl.size) {
            for (y in (x + 1) until (dl.size)) {
                val joinAt = dl[x] intersect dl[y].toSet()
                if (joinAt.size == 1) {
                    val inset = dl[y].toMutableList()
                    Collections.rotate(inset, -1 * (dl[y].indexOf(joinAt.first())))
                    val insertInto = dl[x].toMutableList()
                    val xIndex = dl[x].indexOf(joinAt.first())
                    insertInto.addAll(xIndex, inset)
                    dl.add(insertInto)
                }
            }
        }
        return this
    }

    fun hasDeadlocks(): Boolean {
        return circularDependencies.size != 0 //NO SIR NO
    }

    override fun toString(): String {
        return "Graph(hasDL:${hasDeadlocks()} DL:$circularDependencies)"
    }

    fun reduceBasic(): Graph<T> {
        circularDependencies.retainAll { dl ->
            dl.any {
                it.hasRailSignal()
            }
        }
        return this
    }
}

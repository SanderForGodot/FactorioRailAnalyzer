package graph

import Grafabel
import java.util.*


fun <T : Grafabel> ArrayList<T>.tiernan(fn: (T) -> ArrayList<T>?): Graf<T> {


    val g = Graf<T>()
    this.sorted()
    this.forEach {
        it.expandPath(g, fn)
    }
    return g.unionCircuits().reduceBasic()

}

fun <T : Grafabel> T.expandPath(graf: Graf<T>, fn: (T) -> ArrayList<T>?) {
    graf.path.add(this)

    fn.invoke(this)?.forEach { neighbor ->
        /*1. The extension vertex cannot be in P.*/
        if (!graf.path.contains(neighbor)
            /*2. The extension vertex value must be larger than that of the first vertex of P.*/
            && (neighbor.uniqueID() > graf.path.first().uniqueID())
            /*3. The extension vertex cannot be closed to the last vertex in P.
                 H contains the list of vertices closed to each vertex*/
            && graf.visited[this]?.contains(neighbor) != true
        ) {
            neighbor.expandPath(graf, fn)
            graf.visited(this, neighbor)
        } else {
            if (graf.path.first() == neighbor) {
                graf.addPath()
            }
        }
    }
    graf.visited.remove(this)
    graf.path.removeLast()
}

class Graf<T : Grafabel> {
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

    fun unionCircuits(): Graf<T> {
        val dl = circularDependencies
        for (x in 0 until dl.size) {
            for (y in (x + 1) until (dl.size)) {
                val joinAt = dl[x] intersect dl[y]
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

    fun hasCircutes(): Boolean {
        return circularDependencies.size != 0; //NO SIR NO
    }

    override fun toString(): String {
        return "Graf(hasCD:${hasCircutes()} DL:$circularDependencies)"
    }

    fun reduceBasic(): Graf<T> {
        circularDependencies.retainAll { dl ->
            dl.any {
                it.hasRailSignal()
            }
        }
        return this
    }
}


fun <T : Grafabel> ArrayList<T>.tiernanWithref(fn: (T) -> ArrayList<T>?, ref: (T) -> Grafabel): Graf<Grafabel> {
    val g = Graf<Grafabel>()
    this.sorted()
    this.forEach {
        it.expandPathWithRef(g, fn, ref)
    }
    return g
}

fun <T : Grafabel> T.expandPathWithRef(graf: Graf<Grafabel>, fn: (T) -> ArrayList<T>?, ref: (T) -> Grafabel) {
    graf.path.add(ref.invoke(this))
    fn.invoke(this)?.forEach { neighbor ->
        if (!graf.path.contains(ref.invoke(neighbor))
            && (ref.invoke(neighbor).uniqueID() > ref.invoke(this).uniqueID())
            && graf.visited[ref.invoke(this)]?.contains(ref.invoke(neighbor)) != true
        ) {
            neighbor.expandPathWithRef(graf, fn, ref)
            graf.visited(ref.invoke(this), ref.invoke(neighbor))
        } else {
            if (graf.path.first() == ref.invoke(neighbor)) {
                graf.addPath()
            }
        }
    }
    graf.visited.remove(ref.invoke(this))
    graf.path.removeLast()
}







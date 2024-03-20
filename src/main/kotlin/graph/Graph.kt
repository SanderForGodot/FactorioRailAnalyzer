package graph

import Grafabel


fun <T : Grafabel> ArrayList<T>.tiernan(fn: (T) -> ArrayList<T>?): Graf<T> {


    val g = Graf<T>()
    this.sorted()
    this.forEach {
        it.expandPath(g, fn)
    }
    return g
}

fun <T : Grafabel> T.expandPath(graf: Graf<T>, fn: (T) -> ArrayList<T>?) {
    graf.path.add(this)

    fn.invoke(this)?.forEach { neighbor ->
        /*1. The extension vertex cannot be in P.*/
        if (!graf.path.contains(neighbor)
            /*2. The extension vertex value must be larger than that of the first vertex of P.*/
            && (neighbor.uniqueID() > this.uniqueID())
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

class Graf<T> {
    val path: MutableList<T> = ArrayList()
    var visited: MutableMap<T, MutableList<T>> = mutableMapOf()
    var circularDependencies: MutableList<Set<T>> = mutableListOf()
    fun visited(node: T, neighbor: T) {
        if (visited[node] == null) {
            visited[node] = mutableListOf(neighbor)
        } else {
            visited[node]!!.add(neighbor)
        }
    }

    fun addPath() {
        circularDependencies.add(path.toSet())
    }

    fun hasCircutes(): Boolean {
        return circularDependencies.size != 0; //NO SIR NO
    }

    override fun toString(): String {
        return "Graf(hasCD:${hasCircutes()} DL:$circularDependencies)"
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







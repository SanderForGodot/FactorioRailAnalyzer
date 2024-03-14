package graph

import Block
import Edge
import Grafabel

fun main() {
    var g = Graph()
    var e = Edge()
    var b = Block(e, 1)


    e.nextEdgeListAL().tmTm {
        it.wasIchBeobachte
    }


}

fun <T : Grafabel> ArrayList<T>.tmTm(fn: (T) -> ArrayList<T>?): Boolean {
    val g = grafCompanianCube<T>()
    this.sorted()
    this.forEach {
        it.exP(g, fn)
    }
    return g.DOwEhAVEapROBLEMpRIVATE()
}

fun <T : Grafabel> T.exP(graf: grafCompanianCube<T>, fn: (T) -> ArrayList<T>?) {
    graf.path.add(this)
    fn.invoke(this)?.forEach { neighbor ->
        /*1. The extension vertex cannot be in P.*/
        if (graf.path.contains(neighbor)
            /*2. The extension vertex value must be larger than that of the first vertex of P.*/
            && (neighbor.uniqueID() > this.uniqueID())
            /*3. The extension vertex cannot be closed to the last vertex in P.
                 H contains the list of vertices closed to each vertex*/
            && (graf.visited[this]?.contains(neighbor) == false)
        ) {
            neighbor.exP(graf, fn)
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

class grafCompanianCube<T> {
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

    fun DOwEhAVEapROBLEMpRIVATE(): Boolean {
        return circularDependencies.size == 0; //NO SIR NO
    }


}

class Graph {
    private val path: MutableList<Block> = ArrayList()
    private var visited: MutableMap<Block, MutableList<Block>> = mutableMapOf()
    private var circularDependencies: MutableList<Set<Block>> = mutableListOf()


    fun tiernan(blocklist: ArrayList<Block>) {// important Map must be sorted or the Tiernan will not work

        println(blocklist)
        blocklist.forEach {
            println("Startet Path: ${it.id}")
            expandPath(it)
            visited = mutableMapOf()
        }
        //funfunfun()
    }


    private fun expandPath(node: Block) {
        path.add(node)
        if (visited[node] == null) {
            visited[node] = mutableListOf()
        }

        for (neighbor in node.neighbourBlocks()) {
            //visited[node]?.let { println(it.size) }
            if (!path.contains(neighbor)

                and (neighbor.id > path.first().id)
                and ((visited[node]?.contains(neighbor) == false))
            ) {
                //println("Expanding to: $neighbor")
                expandPath(neighbor)
                visited[node]?.add(neighbor)
            } else {
                if (path.first() == neighbor) {
                    println("Found Deadlock: $path")
                    if (circularDependencies.isEmpty()) {//needs to done, so that .last() call works
                        circularDependencies.add(path.toSet())
                    } else {
                        if (!arePDlsTheSame(
                                circularDependencies.last(),
                                path.toSet()
                            )
                        ) {//Don't add the same deadlock twice
                            circularDependencies.add(path.toSet())
                        }
                    }
                }
            }
        }

        visited[node] = mutableListOf()
        //println("removing node:" + path.last())
        path.removeLast()
    }

    fun funfunfun() {
        circularDependencies.forEach { potentialDeadlock ->
            if (potentialDeadlock.any { block -> block.hasRailSignal() }) {
                //case 1
                potentialDeadlock.filter { block -> block.hasRailSignal() }.forEach { block ->

                }
            } else {
                // do Stuff B
            }
            if (potentialDeadlock.any { block -> block.hasRailSignal() }) {
                // potentialDeadlock is a true deadlock
                // we coud do furthur analisis on this to deteimain the max save trainlength to not cause a dl
            } else {
                if (isDrivable(potentialDeadlock.toMutableList())) {
                    var a = "removes the error tmp"
                    // get the list of entitys in the dependency and calulate max size
                } else {
                    //potentialDeadlock is no deadlock
                }
                // do Stuff B
            }
        }
    }

    fun arePDlsTheSame(pDl1: Set<Block>, pDl2: Set<Block>): Boolean {
        var str1: String = pDl1.map { block -> block.id }.joinToString { int -> int.toString() }
        val str2: String = pDl2.map { block -> block.id }.joinToString { int -> int.toString() }
        return str1.contains(str2) and str2.contains(str1)
    }


    private fun isDrivable(potentialDeadlock: MutableList<Block>): Boolean {
        assert(potentialDeadlock.size > 2) { "a potentialDeadlock requires to have 2 elements" }
        var pdIterator = potentialDeadlock.iterator()
        //a potentialDeadlock requires to have 2 elements
        var firstBlock = pdIterator.next()//potentialDeadlock[0]
        // to check if the last bit of the circle closes
        potentialDeadlock.add(firstBlock)
        var nextBlock = pdIterator.next() //potentialDeadlock[1]

        // find the first (and only) edge that connects the first 2 blocks
        var firstEdge: Edge = firstBlock.edgeList.firstOrNull { edgeOfTheBlock ->
            edgeOfTheBlock.nextEdgeList!!.any { nextPossible ->  //todo: null force not save checking required
                nextPossible.belongsToBlock == nextBlock
            }
        } ?: return false
        var currentEdge = firstEdge
        //for every following block check if we cn get there from the current Edge
        while (pdIterator.hasNext()) {
            nextBlock = pdIterator.next()
            currentEdge =
                currentEdge.nextEdgeList!!.firstOrNull { nextPossible ->  //todo: null force not save checking required
                    nextPossible.belongsToBlock == nextBlock
                } ?: return false
        }
        return true
    }


    fun getDeadlocks(): MutableList<Set<Block>> {
        return circularDependencies
    }

    fun addDependingOn(node: Block, neighbor: Block) {
    }

    fun determineDeadlocks() {
        circularDependencies.forEach { potentialDeadlock ->
            if (potentialDeadlock.any { block -> block.hasRailSignal() }) {
                potentialDeadlock.filter { block -> block.hasRailSignal() }.forEach inner@{ block ->
                    if (block.hasMixedSignals()) {
                        println("Sorry we can't determine if the Deadlock is absolute, but there is a big possibility for one$potentialDeadlock")
                        return@forEach
                    } else {
                        println("found Deadlock$potentialDeadlock")
                        return@forEach
                    }
                }
            } else {
                println("Deadlock prevented by Chain Signals$potentialDeadlock")
            }
        }
    }


}

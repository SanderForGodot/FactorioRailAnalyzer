package graph

import Block
import Edge


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
        funfunfun()
    }


    private fun expandPath(node: Block) {
        path.add(node)
        if (visited[node] == null) {
            visited[node] = mutableListOf()
        }

        for (neighbor in node.neighbourBlocks()) {
            visited[node]?.let { println(it.size) }
            //addDependingOn(node,neighbor)//for the Graphviz Output
            if (!path.contains(neighbor)
                and (neighbor.id > path.first().id)
                and ((visited[node]?.contains(neighbor) == false))) {
                println("Expanding to: $neighbor")
                expandPath(neighbor)
                visited[node]?.add(neighbor)
            } else {
                if (path.first() == neighbor) {
                    circularDependencies.add(path.toSet())
                }
            }
        }

        visited[node] = mutableListOf()
        println("removing node:" + path.last())
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
        str1 += str1
        return str1.contains(str2)
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
            edgeOfTheBlock.nextEdgeList.any { nextPossible ->
                nextPossible.belongsToBlock == nextBlock
            }
        } ?: return false
        var currentEdge = firstEdge
        //for every following block check if we cn get there from the current Edge
        while (pdIterator.hasNext()) {
            nextBlock = pdIterator.next()
            currentEdge = currentEdge.nextEdgeList.firstOrNull { nextPossible ->
                nextPossible.belongsToBlock == nextBlock
            } ?: return false
        }
        return true
    }


    fun getDeadlocks(): MutableList<Set<Block>> {
        return circularDependencies
    }

    fun addDependingOn(node: Block, neighbor: Block) {
        if (node.dependingOn.isNullOrEmpty()) {
            node.dependingOn = arrayListOf(neighbor)
        } else {
            node.dependingOn!!.add(neighbor)
        }
    }
}
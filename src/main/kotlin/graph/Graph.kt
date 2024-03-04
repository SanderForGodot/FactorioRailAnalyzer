package graph

import Block


class Graph {
    private val path: MutableList<Block> = ArrayList()
    private var testGraph: MutableMap<Int, MutableList<Int>> = mutableMapOf()
    private var visited: MutableMap<Block, MutableList<Block>> = mutableMapOf()
    private var deadlocks: MutableList<Set<Block>> = mutableListOf()


    fun tiernan(blocklist: ArrayList<Block>) {// important Map must be sorted or the Tiernan will not work
        //testGraph=testGraph.toSortedMap()
        println(blocklist)
        blocklist.iterator().forEach {
            println("Startet Path: ${it.id}")
            expandPath(it)
            visited = mutableMapOf()
        }
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
                and ((visited[node]?.contains(neighbor) == false))
            ) {
                println("Expanding to: $neighbor")
                expandPath(neighbor)
                visited[node]?.add(neighbor)
            } else {
                if (path.first() == neighbor) {
                    if(path.any{it.isRelevant}){
                        println("found deadlock with node: ${path.first()}")
                        println(path)
                        deadlocks.add(path.toSet())
                    } else {
                        println("found circle, that's not a deadlock")
                        println(path)
                    }
                } else {
                    println("no circuit at path-end: $node")
                }

            }

        }

        visited[node] = mutableListOf()
        println("removing node:" + path.last())
        path.removeLast()
    }

    fun gentestgraph() {
        testGraph[1] = mutableListOf(2)
        testGraph[2] = mutableListOf(3, 4)
        testGraph[3] = mutableListOf(1)
        testGraph[4] = mutableListOf(5)
        testGraph[5] = mutableListOf(2)
    }

    /* Graph from the Tiernan Paper
     testGraph[1] = mutableListOf(2)
     testGraph[2] = mutableListOf(2,3,4)
     testGraph[3] = mutableListOf(5)
     testGraph[4] = mutableListOf(3)
     testGraph[5] = mutableListOf(1)
     */


    fun setGraph(graph: MutableMap<Int, MutableList<Int>>) {
        testGraph = graph
    }

    fun getDeadlocks(): MutableList<Set<Block>> {
        return deadlocks
    }

    fun addDependingOn(node: Block,neighbor:Block){
        if( node.dependingOn.isNullOrEmpty()){node.dependingOn= arrayListOf(neighbor)
        }else{
            node.dependingOn!!.add(neighbor)}
    }
}
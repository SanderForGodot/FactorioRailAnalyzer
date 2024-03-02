package graph


class Graph {
    private val path: MutableList<Int> = ArrayList()
    private var testGraph: MutableMap<Int, MutableList<Int>> = mutableMapOf()
    private var closedVertices: MutableMap<Int,MutableList<Int>> =  mutableMapOf()
    private var deadlocks: MutableList<MutableList<Int>> = mutableListOf()



    fun tiernan() {// important Map must be sorted or the Tiernan will not work
        //testGraph=testGraph.toSortedMap()
        println(testGraph)
        testGraph.iterator().forEach {
            println("Startet Path: ${it.key}")
            expandPath(it.key)
            closedVertices =  mutableMapOf()
        }
    }

    private fun expandPath(node: Int) {
        path.add(node)
        if(closedVertices[node]==null){
            closedVertices[node] = mutableListOf()
        }


        if (testGraph[node] != null) {
            for (neighbor in testGraph[node]!!) {
                closedVertices[node]?.let { println(it.size) }
                if (!path.contains(neighbor)
                    and (neighbor > path.first())
                    and ( (closedVertices[node]?.contains(neighbor) == false))
                ) {
                    println("Expanding to: $neighbor")
                    expandPath(neighbor)
                    closedVertices[node]?.add(neighbor)
                } else {
                    if(path.first()==neighbor){//TODO: maybe change to Path.contains(neighbour), when the analies only starts at the entrypoints
                        println("found deadlock ending in node: $node")
                        println(path)
                        deadlocks.add(path)
                    }else{
                        println("no circuit at path-end: $node")
                    }

                }

            }
        } else {
            println("No neighbours: $node")
        }
        closedVertices[node]=mutableListOf()
        println("removing node:"+ path.last())
        path.removeLast()
    }

    fun gentestgraph(){
        testGraph[1] = mutableListOf(2)
        testGraph[2] = mutableListOf(3,4)
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

    private fun dfs(node: Int, visited: BooleanArray, graph: Array<List<Int>>) {
        //possible algo Depth-First Search
        // zur Kreiserkennung nutzbar laut https://medium.com/@AlexanderObregon/introduction-to-graph-algorithms-in-java-a-beginners-guide-450cace790d4
        visited[node] = true
        println("Visited node: $node")

        for (neighbor in graph[node]) {
            if (!visited[neighbor]) {
                dfs(neighbor, visited, graph)
            } else {
                println("found deadlock ending in node: $node")
                println(path)
            }
        }

    }
    fun setGraph(graph:MutableMap<Int, MutableList<Int>>){
        testGraph =graph
    }

    fun getDeadlocks(): MutableList<MutableList<Int>> {
        return deadlocks
    }
}
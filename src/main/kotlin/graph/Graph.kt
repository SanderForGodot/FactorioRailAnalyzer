package graph


class Graph {
    private val adjVertices: MutableMap<GraphNode, MutableList<GraphNode>> = mutableMapOf()
    private val Path: MutableList<Int> = ArrayList()
    private var testGraph: MutableMap<Int, MutableList<Int>> = mutableMapOf()
    private var closedVertices: MutableMap<Int,MutableList<Int>> =  mutableMapOf()

    fun addNode(id: Int) {
        adjVertices.putIfAbsent(GraphNode(id), ArrayList<GraphNode>())
    }

    fun removeNode(id: Int) {
        val v = GraphNode(id)
        adjVertices.values.stream().forEach { list -> list.drop(list.indexOf(v)) }
        adjVertices.remove(GraphNode(id))
    }

    fun addDirectionalEdge(id1: Int, id2: Int) {
        val v1 = GraphNode(id1)
        val v2 = GraphNode(id2)
        adjVertices.getValue(v1).add(v2)
    }

    fun tiernan() {// important Map must be sorted or the Tiernan will not work
        testGraph=testGraph.toSortedMap()
        println(testGraph)
        testGraph.iterator().forEach {
            println("Startet Path: ${it.key}")
            expandPath(it.key)
            closedVertices =  mutableMapOf()
        }
    }

    fun expandPath(node: Int) {
        Path.add(node)
        if(closedVertices[node]==null){
            closedVertices[node] = mutableListOf()
        }


        if (testGraph[node] != null) {
            for (neighbor in testGraph[node]!!) {
                closedVertices[node]?.let { println(it.size) }
                if (!Path.contains(neighbor)
                    and (neighbor > Path.first())
                    and ( (closedVertices[node]?.contains(neighbor) == false))
                ) {
                    println("Expanding to: $neighbor")
                    expandPath(neighbor)
                    closedVertices[node]?.add(neighbor)
                } else {
                    if(Path.first()==neighbor){
                        println("found deadlock ending in node: $node")
                        println(Path)
                    }else{
                        println("no circuit at path-end: $node")
                    }

                }

            }
        } else {
            println("No neighbours: $node")
        }
        closedVertices[node]=mutableListOf()
        println("removing node:"+ Path.last())
        Path.removeLast()
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

    fun dfs(node: Int, visited: BooleanArray, graph: Array<List<Int>>) {
        //possible algo Depth-First Search
        // zur Kreiserkennung nutzbar laut https://medium.com/@AlexanderObregon/introduction-to-graph-algorithms-in-java-a-beginners-guide-450cace790d4
        visited[node] = true
        println("Visited node: $node")

        for (neighbor in graph[node]) {
            if (!visited[neighbor]) {
                dfs(neighbor, visited, graph)
            } else {
                println("found deadlock ending in node: $node")
                println(Path)
            }
        }

    }
}
package graph

import java.util.*
import kotlin.collections.ArrayList


class Graph {

    private val Path: MutableList<GraphNode> = ArrayList()
    private var testGraph: MutableMap<GraphNode, MutableList<GraphNode>> = mutableMapOf()
    private var closedVertices: MutableMap<Int,MutableList<Int>> =  mutableMapOf()

    fun addNodeById(id: Int):GraphNode {
        val node = GraphNode(id)
        testGraph.putIfAbsent(node, mutableListOf())
        return node
    }

    fun addNode(node: GraphNode) {
        testGraph.putIfAbsent(node, mutableListOf())
    }

    fun addNeighbour(node: GraphNode,neighbour:GraphNode){
        addNode(node)
        addNode(neighbour)
        testGraph[node]!!.add(neighbour)
    }

    fun addNeighbourById(nodeId: Int,neighbourId:Int){
        val node = addNodeById(nodeId)
        val neighbour =addNodeById(neighbourId)
        testGraph[node]!!.add(neighbour)
    }

    fun addNeighbours(node: GraphNode,neighbours:MutableList<GraphNode> ){
        addNode(node)
        neighbours.forEach {
            addNode(it)
            testGraph[node]!!.add(it)
        }
    }

    fun findNodeById(id:Int): GraphNode {
       testGraph.keys.forEach{
           if (it.id==id){
               return it
           }
       }
        return addNodeById(id)
    }


    fun tiernan() {// important Map must be sorted or the Tiernan will not work
        println(testGraph)
        testGraph.iterator().forEach {
            println("Startet Path: ${it.key}")
            expandPath(it.key)
            closedVertices =  mutableMapOf()
        }
    }

    fun expandPath(node: GraphNode) {
        Path.add(node)
        if(closedVertices[node.id]==null){
            closedVertices[node.id] = mutableListOf()
        }
        if (testGraph[node] != null) {
            for (neighbor in testGraph[node]!!) {

                if (!Path.contains(neighbor)
                    and (neighbor.id > Path.first().id)
                    and ( (closedVertices[node.id]?.contains(neighbor.id) == false))
                ) {
                    println("Expanding to:$neighbor")
                    expandPath(neighbor)
                    closedVertices[node.id]?.add(neighbor.id)
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
        closedVertices[node.id]=mutableListOf()
        println("removing node:"+ Path.last())
        Path.removeLast()
    }

    fun gentestgraph(){
        val node1= addNodeById(1)
        val node2=addNodeById(2)
        val node3=addNodeById(3)
        val node4= addNodeById(4)
        val node5=addNodeById(5)
        addNeighbour(node1,node2)
        addNeighbours(node2, mutableListOf(node2,node3,node4))
        addNeighbour(node3,node5)
        addNeighbour(node4,node3)
        addNeighbour(node5,node1)
    }

    /* Graph from the Tiernan Paper
     val node1= addNodeById(1)
        val node2=addNodeById(2)
        val node3=addNodeById(3)
        val node4= addNodeById(4)
        val node5=addNodeById(5)
        addNeighbour(node1,node2)
        addNeighbours(node2, mutableListOf(node2,node3,node4))
        addNeighbour(node3,node5)
        addNeighbour(node4,node3)
        addNeighbour(node5,node1)
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

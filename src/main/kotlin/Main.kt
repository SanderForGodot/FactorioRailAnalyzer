import com.google.gson.Gson
import factorioBlueprint.Entity
import factorioBlueprint.ResultBP
import graph.Graph

fun main(args: Array<String>) {

    println("Program arguments: ${args.joinToString()}")

    //region Phase0: data decompression
    val jsonString: String = decodeBpSting("decodeTest.txt")
    if (jsonString.contains("blueprint_book")){throw Exception("Sorry, a Blueprintbook cannot be parsed by this Programm, please input only Blueprints ")}
    val resultBP : ResultBP =Gson().fromJson(jsonString, ResultBP::class.java)
    val entityList = resultBP.blueprint.entities
    //endregion
    //region Phase1: data cleansing and preparation
    //filter out entity's we don't care about
    //ordered by (guessed) amount they appear in a BP
    entityList.retainAll {
         it.entityType != null //it can be null the ide is lying (GSON brakes kotlin null safety)
    }
    // determinant min and max of BP
    val (min, max) = entityList.determineMinMax()
    // normalize coordinate space to start at 1, 1 (this makes the top left rail-corner be at 0.0)
    max -= min
    entityList.forEach { entity ->
        entity.position = entity.position - min
    }

    val matrix = entityList.filedMatrix(max)
    //endregion
    //region Phase2: rail Linker: connected rails point to each other with pointer list does the same with signals
    entityList.railLinker(matrix)
    //endregion
    Graphviz().generateEntityRelations(entityList)
    //region Phase3: create
    //prepare
    val signalList: List<Entity> = entityList.filter { entity ->
        entity.isSignal()
    }

    if (signalList.isEmpty())
        throw Exception("No Edges Found, because there are no signals in the blueprint")

    val listOfEdges = arrayListOf<Edge>()
    val relation = mutableMapOf<Entity, ArrayList<Edge>>()
    signalList.forEach { startPoint ->
        relation[startPoint] = buildEdge(Edge(startPoint), if (startPoint.direction < 4) -1 else 1)
        listOfEdges.addAll(relation[startPoint]!!)
    }

    val notStartSignalList = arrayListOf<Entity>()

    listOfEdges.filter { edge ->
        val signal = edge.last(1)
        signal.isSignal() && signal.entityType != EntityType.VirtualSignal
                && edge.validRail!!
    }.forEach { edge ->
        val endingSignal = edge.last(1)
        edge.nextEdgeList = relation[endingSignal]!!
        notStartSignalList.addUnique(endingSignal)
    }
    //endregion
    // guard check point
    if (listOfEdges.size == 0) {
        throw Exception("No Edges Found, but there are some signals ")
    }
    //region Phase4: creating the blocks that are defined by the signals in factorio

    val blockList = connectEdgesToBlocks(listOfEdges)
    // creating the Graph out of the Blocks and edges
    println("relevante blöcke")
    val startSignals = signalList.toSet() - notStartSignalList.toSet()

    val graph: MutableMap<Int, MutableList<Int>> = mutableMapOf()
    blockList.forEach { block -> block.markRelevant(startSignals) }
    blockList.filter { block -> block.isRelevant }
        .forEach { block ->
            block.findEnd()
            graph[block.id] =block.dependingOn.map { it.id }.toMutableList()
        }

    //analysing the graph
    val graphTesting = Graph()
    graphTesting.setGraph(graph)
    graphTesting.tiernan()

    //debug output
    var i = 0
    listOfEdges.forEach {
       // println(it)
        Graphviz().printEdge(it, i)
        i++

    }

    println("joooo")
    Graphviz().printGraph(graph)
    Graphviz().printBlocks(blockList)
    blockList.forEach {
        println("id:" + it.id + " center: " + it.calculateCenter())
    }

    println(graph)
    //endregion
}

import com.google.gson.Gson
import factorioBlueprint.Blueprint
import factorioBlueprint.Entity
import factorioBlueprint.ResultBP
import graph.Graph

fun main(args: Array<String>) {

    println("Program arguments: ${args.joinToString()}")

    //TODO: @Leos task

    factorioRailAnalyzer("decodeTest.txt" /*,options*/) //please document the options before coding

}
fun factorioRailAnalyzer(blueprint: String){
    //region Phase0: data decompression
    val jsonString: String = decodeBpSting(blueprint)
    if (jsonString.contains("blueprint_book")) {
        throw Exception("Sorry, a Blueprintbook cannot be parsed by this Programm, please input only Blueprints ")
    }
    val resultBP: ResultBP = Gson().fromJson(jsonString, ResultBP::class.java)
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
    //Graphviz().generateEntityRelations(entityList)
    //region Phase3: create
    //prepare
    val signalList: List<Entity> = entityList.filter { entity ->
        entity.isSignal()
    }

    if (signalList.isEmpty())
        throw Exception("No Edges Found, because there are no signals in the blueprint") //todo: construction error

    val listOfEdges = arrayListOf<Edge>()
    val relation = mutableMapOf<Entity, ArrayList<Edge>>()
    signalList.forEach { startPoint ->
        val resultEdges = arrayListOf<Edge>()
        if (startPoint.rightNextRail.size > 0)
            resultEdges.addAll(buildEdge(Edge(startPoint), 1))
        if (startPoint.leftNextRail.size > 0)
            resultEdges.addAll(buildEdge(Edge(startPoint), -1))
        if (resultEdges.size > 0) {
            relation[startPoint] = resultEdges
            listOfEdges.addAll(resultEdges)
        }
    }
    svgFromPoints(listOfEdges)

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


    println("x points to y")
    listOfEdges.forEach { edge ->
        print("\n" + edge.aToB() + "  to  ")
        edge.nextEdgeList.forEach {
            print(it.aToB() + ", ")
        }

    }
    println("\nx points to y")
    //endregion
    // guard check point
    if (listOfEdges.size == 0) {
        throw Exception("No Edges Found, but there are some signals ")  //todo: unexpected  error
    }
    //region Phase4: creating the blocks that are defined by the signals in factorio

    val blockList = connectEdgesToBlocks(listOfEdges)
    // creating the Graph out of the Blocks and edges
    println("relevante blöcke")
    val startSignals = signalList.toSet() - notStartSignalList.toSet()

    var cnt = 0
    startSignals.forEach { startSig ->
        cnt--
        var virtualSig = Entity(0, EntityType.VirtualSignal)
        var startEdge = Edge(Edge(virtualSig), startSig)
        startEdge.nextEdgeList = relation[startSig]?: return@forEach
        listOfEdges.add(startEdge)
        var startBlock = Block(startEdge, cnt)
        startEdge.belongsToBlock = startBlock
        blockList.add(startBlock)
    }
    listOfEdges.filter { edge ->
        edge.belongsToBlock!!.id < 0 //aka ist start block
                || edge.entityList.first().entityType == EntityType.VirtualSignal // ggf eine bessere alternaive start edges fest zustellen
                || edge.entityList.first().entityType == EntityType.Signal
    }.filter {
        edge ->  edge.validRail!!
    }.forEach{
        edge->edge.setzteBeobachtendeEdges()
    }
   // Graphviz().printBlocksFromEdgeRelations(listOfEdges)


    //analysing the graph
    val graphTesting = Graph()
    graphTesting.tiernan(blockList)

    //debug output
    var i = 0
    listOfEdges.forEach {
        // println(it)
       // Graphviz().printEdge(it, i)
        i++

    }

    println("joooo")
    //Graphviz().printBlocks(blockList)
    blockList.forEach {
        println("id:" + it.id + " center: " + it.calculateCenter())
    }

    //endregion
    println("found Deadlocks" + graphTesting.getDeadlocks())


}

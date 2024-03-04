import com.google.gson.Gson
import factorioBlueprint.Entity
import factorioBlueprint.ResultBP

/*
fun main() {
    val BlockNumberTest1=8
    val BlockNumberTest2=4


    //test("src/test/resources/testBPs/curvecollisionstraight.txt", BlockNumberTest1)
    test("src/test/resources/testBPs/curvecollisiondiagonal.txt", BlockNumberTest1)
    //test("src/test/resources/testBPs/curvecollision.txt", BlockNumberTest2)
    //test("src/test/resources/testBPs/curvecollisiondoubleside.txt", BlockNumberTest2)

}
fun test(filename:String, solution:Int){

    //region Phase0: data decompression
    val jsonString: String = decodeBpSting(filename)
    if (jsonString.contains("blueprint_book")){throw Exception("Sorry, a Blueprintbook cannot be parsed by this Programm, please input only Blueprints ")}
    val resultBP : ResultBP =Gson().fromJson(jsonString, ResultBP::class.java)
    val entityList = resultBP.blueprint.entities
    //endregion
    //region Phase1: data cleansing and preparation
    //filter out entity's we don't care about
    //ordered by (guessed) amount they appear in a BP
    entityList.retainAll {
        it.entityType != null //it can be null the ide is lying
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
        var resultEdges = arrayListOf<Edge>()
        if(startPoint.rightNextRail.size>0)
            resultEdges.addAll( buildEdge(Edge(startPoint),  1))
        if(startPoint.leftNextRail.size>0)
            resultEdges.addAll( buildEdge(Edge(startPoint), -1 ))
        if (resultEdges.size>0) {
            relation[startPoint] = resultEdges
            listOfEdges.addAll(resultEdges)
        }
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
/* // this was the only place wich pointed to deprecaded code
    val graph: MutableMap<Int, MutableList<Int>> = mutableMapOf()
    blockList.forEach { block -> block.markRelevant(startSignals) }
    blockList.filter { block -> block.isRelevant }
        .forEach { block ->
            block.findEnd()
            graph[block.id] = block.dependingOn?.map { it.id }!!.toMutableList()
        }
*/
    //debug output
    var i = 0
    listOfEdges.forEach {
        // println(it)
        Graphviz().printEdge(it, i)
        i++

    }

    println("joooo")
   // Graphviz().printGraph(graph)
    //Graphviz().printBlocks(blockList)
    blockList.forEach {
        println("id:" + it.id + " center: " + it.calculateCenter())
    }

   // println(graph)
    //endregion
     assert(blockList.size==solution){"Blocksize is not the same as the expected value$solution, but rather:${blockList.size}"}


}
*/

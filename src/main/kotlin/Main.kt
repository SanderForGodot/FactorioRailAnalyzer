import com.google.gson.Gson
import factorioBlueprint.Entity
import factorioBlueprint.Position
import factorioBlueprint.ResultBP
import graph.Graph
import java.nio.file.Files
import java.nio.file.Path

//Boolean Options TODO:Implement those fuckers
var GraphvizOutput = false
var InstantShowOutput = false
var showdebug = true
var edgesGetRandomColor = true
var usesInputFile = true


fun main(args: Array<String>) {

    println("Program arguments: ${args.joinToString()}")

    val options = args.filter { it.startsWith("-") }
    val inputBlueprintString = args.filter { it.startsWith("0") }
    if (inputBlueprintString.size > 1) {
        println("Too Many Blueprints provided")
        return
    }/*else if (inputBlueprintString.size<1){ // Add for CLI Builds
        println("No Blueprint provided")
        return
    }*/

    var jsonString: String = ""
    if (inputBlueprintString.size == 1) {
        jsonString = decodeBpString(inputBlueprintString.first())
    }
    if (Files.exists(Path.of("decodeTest.txt"))) {
        //TODO: @Leos task
        factorioRailAnalyzer("decodeTest.txt" /*,options*/) //please document the options before coding
    }
}

fun factorioRailAnalyzer(blueprint: String) {
    //region Phase0: data decompression
    val jsonString = decodeBpStringFromFilename(blueprint)
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
        throw Exception("No Edges Found, but there are some signals ")  //todo: unexpected  error
    }
    //region Phase4: creating the blocks that are defined by the signals in factorio

    val blockList = connectEdgesToBlocks(listOfEdges)
    var pos =  simpleCheck(listOfEdges)
    svgFromPoints(max, listOfEdges, signalList,pos)

    // creating the Graph out of the Blocks and edges
    println("relevante blöcke")
    val startSignals = signalList.toSet() - notStartSignalList.toSet()



    var cnt = 0
    startSignals.forEach { startSig ->
        cnt--
        var virtualSig = Entity(0, EntityType.VirtualSignal)
        var startEdge = Edge(Edge(virtualSig), startSig)
        startEdge.nextEdgeList = relation[startSig] ?: return@forEach
        listOfEdges.add(startEdge)
        var startBlock = Block(startEdge, cnt)
        startEdge.belongsToBlock = startBlock
        blockList.add(startBlock)
    }
    listOfEdges.filter { edge ->
        edge.belongsToBlock!!.id < 0 //aka ist start block
                || edge.entityList.first().entityType == EntityType.VirtualSignal // ggf eine bessere alternaive start edges fest zustellen
                || edge.entityList.first().entityType == EntityType.Signal
    }.filter { edge ->
        edge.validRail!!
    }.forEach { edge ->
        edge.setzteBeobachtendeEdges()
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
    Graphviz().printBlocks(blockList)
    blockList.forEach {
        println("id:" + it.id + " center: " + it.calculateCenter())
    }

    //endregion
    println("found Deadlocks" + graphTesting.getDeadlocks())
    graphTesting.determineDeadlocks()

}


fun simpleCheck(listOfEdges: ArrayList<Edge>): Position{
    if (!listOfEdges.filter { edge ->
            edge.last(1).entityType != EntityType.VirtualSignal
        }.any { edge ->
            edge.entityList.first().entityType == EntityType.Signal
        }) {
        //all signals are chain signals with exception to ending signals
        // this guaranties no deadlock
        // return ""
        println("simple check concluded: no deadlock")
    } else {
        println("simple check concluded: potential deadlock")
        var minSize = listOfEdges.filter { edge ->
            edge.entityList.first().entityType == EntityType.Signal
                    && edge.last(1).entityType != EntityType.VirtualSignal
        }.minBy { edge ->
            edge.tileLength
        }
        println("max tile length: ${minSize.tileLength} or ${Math.floor(minSize.tileLength/6.5)} train cars")
        return minSize.entityList.first().position
    }
return Position(-1.0, -1.0)

}
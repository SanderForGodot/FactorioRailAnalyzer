import com.google.gson.Gson
import factorioBlueprint.Entity
import factorioBlueprint.Position
import factorioBlueprint.ResultBP
import graph.Graph
import graph.tiernan
import graph.tiernanWithref
import java.nio.file.Files
import java.nio.file.Path
import java.util.*

fun main(args: Array<String>) {

    val options = args.filter { it.startsWith("-") }
    setCLIOptions(options)
    dbgPrintln("Program arguments: ${args.joinToString()}")
    val inputBlueprintString = args.filter { it.startsWith("0") }
    val blueprintFile = args.filter { !it.startsWith("-") }
    if (inputBlueprintString.size > 1) {
        println("Too Many Blueprints provided")
        return
    }/*else if (inputBlueprintString.size<1){ // Add for CLI Builds
        println("No Blueprint provided")
        return
    }*/

    val jsonString: String
    if (inputBlueprintString.size == 1) {
        jsonString = decodeBpString(inputBlueprintString.first())
    } else {
        if (blueprintFile.isNotEmpty()) {
            if (Files.exists(Path.of(blueprintFile.first()))) {
                jsonString = decodeBpStringFromFilename(blueprintFile.first())

            } else {
                println("File not Found")
                return
            }
        } else {
            if (Files.exists(Path.of("decodeTest.txt"))) {
                jsonString = decodeBpStringFromFilename("decodeTest.txt")//default
            } else {
                println("No Blueprint provided")
                return
            }
        }
    }
    NEWfactorioRailAnalyzer(jsonString)

}


fun factorioRailAnalyzer(blueprint: String): Boolean {
    //region Phase0: data decompression
    if (blueprint.contains("blueprint_book")) {
        throw Exception("Sorry, a blueprint book cannot be parsed by this program, please input only Blueprints ")
    }
    val resultBP: ResultBP = Gson().fromJson(blueprint, ResultBP::class.java)
    val entityList = resultBP.blueprint.entities
    //endregion
    //region Phase1: data cleansing and preparation
    //filter out entity's we don't care about
    entityList.removeAll {
        it.entityType == null //it can be null the ide is lying (GSON brakes kotlin null safety)
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
    //todo decide if this can be removed entirely as it was mostly just debug
    if (entityList.size < 100) Graphviz().generateEntityRelations(entityList)
    //region Phase3: create
    //prepare
    val signalList: List<Entity> = entityList.filter { entity ->
        entity.isSignal()
    }

    if (signalList.isEmpty()) throw Exception("No Edges Found, because there are no signals in the blueprint")  //todo: create custom construction error

    //create forward facing edges

    var relation = signalList.map {
        (it.entityNumber!!) to
                buildEdge(it)
    }.groupBy({
        it.first
    }, {
        it.second
    }).mapValues { it ->
        it.value.flatten().distinctBy { it.uniqueID() }
            .onEach { it.cleanAndCalc() }
            .toMutableList()
    }

    var listOfEdges = relation.map { it.value }.flatten().toMutableList() as ArrayList<Edge>
    val backwardsEdges = signalList.map {
        buildEdgeReversed(it)
    }.flatten()
    listOfEdges.addAll(backwardsEdges)


//set next possible edges per edge
    listOfEdges.filter { edge ->
        edge.last(1).entityType != EntityType.VirtualSignal && edge.validRail
    }.forEach { edge ->
        edge.nextEdgeList = relation[edge.last(1).entityNumber]
    }
    backwardsEdges.forEach { it.setEntry() }


//endregion

//region Phase4: creating the blocks that are defined by the signals in factorio

    val blockList = connectEdgesToBlocks(listOfEdges)

    //var pos = simpleCheck(listOfEdges)
//pos = entityList.find { it.entityNumber ==20 }?.position ?: Position(0.0,0.0)
    svgFromPoints(max, listOfEdges, signalList, Position(-10.0, -10.0))

// creating the Graph out of the Blocks and edges


    listOfEdges.filter { edge ->
        (edge.belongsToBlock?.anyEntryFlag() == true
                || edge.hasRailSignal())
                && edge.validRail
    }.forEach { edge ->
        edge.monitor()
    }

    listOfEdges.forEach { edge ->
        edge.monitoredEdgeList.forEach {
            edge.belongsToBlock!!.dependingOn.addUnique(it.belongsToBlock!!)
        }
    }
// Graphviz().printBlocksFromEdgeRelations(listOfEdges)


//debug output
    var i = 0
    listOfEdges.forEach {
        dbgPrintln(it)
        if (CLIOptions[CLIFlags.GraphvizOutput]!!) {
            Graphviz().printEdge(it, i)
        }
        i++
    }

    if (CLIOptions[CLIFlags.GraphvizOutput]!!) {
        Graphviz().printBlocks(blockList)
    }

    dbgPrintln {
        blockList.forEach {
            println("id:" + it.id + " center: " + it.calculateCenter())
        }
    }

//endregion


//blockList.visualize("neighbourBlocks"){it.neighbourBlocks()}
    val a = listOfEdges.tiernanWithref({
        it.monitoredEdgeList
    }, {
        it.belongsToBlock!!
    })


    val b = listOfEdges.tiernan {
        it.nextEdgeListAL()
    }

    val c = blockList.tiernan {
        it.directNeighbours()
    }.reduceBasic().analysis()
    val d = blockList.tiernan {
        it.dependingOn
    }
    println(c)


    blockList.visualize("neighborBlocks", c) { it.directNeighbours() }
    blockList.visualize("blockDependency", d) { it.dependingOn }
    listOfEdges.visualizeWithRef("monitoredEdgeList", { it.monitoredEdgeList }) { it.belongsToBlock }
//listOfEdges.visualizeWithRef("nextEdgeListAL"){it.nextEdgeListAL()}
//blockList.visualizeWithRef("neighbourBlocks"){it.neighbourBlocks()}
// Edit here to Test different systems
    return c.hasDeadlocks()
}

fun Graph<Block>.analysis(): Graph<Block> {

    this.circularDependencies.retainAll {
        it.analysis()
    }
    return this
}


private fun <E> List<E>.at(i: Int): E {
    return this[((i % this.size) + this.size) % this.size]
}

private fun List<Block>.analysis(): Boolean {
    val transSetList = ArrayList<Set<Edge>>()
    val indexList = ArrayList<Int>()
    for (i in this.indices) {
        val r = this.at(i).getEdge(this.at(i - 1), this.at(i + 1))
        transSetList.add(r)
        if (r.size == 2)
            indexList.add(i)
    }
    val transList = transSetList.flatten()
    if (indexList.size == 0)
        return true
    Collections.rotate(transList, -1 * indexList[0])
    for (i in indexList.indices)
        indexList[i] = i + indexList[i] - indexList[0]

    for (i in 0 until indexList.size - 1)
        if (transList.subList(indexList[i], indexList[i + 1]).all {
                it.entityList.first().entityType != EntityType.Signal
            })
            return false
    return true
}

fun Block.getEdge(form: Block, to: Block): Set<Edge> {

    val firstOptions = this.edgeList.filter { edge ->
        form.edgeList.mapNotNull { it.nextEdgeList }.flatten()
            .contains(edge)
    }
    val secondOptions = this.edgeList.filter { edge ->

        edge.nextEdgeList?.mapNotNull { it.belongsToBlock }
            ?.contains(to) == true
    }
    val intersect = firstOptions intersect secondOptions.toSet()
    var union = firstOptions union secondOptions
    if (intersect.size == 1) return intersect
    if (union.size == 2) return union
    val a = firstOptions.toMutableList()
    a.retainAll { edge ->
        secondOptions.any {
            edge.doesCollide(it)
        }
    }
    val b = secondOptions.toMutableList()
    b.retainAll { edge ->
        firstOptions.any {
            edge.doesCollide(it)
        }
    }
    union = a union b
    if (union.size == 2) return union
    throw Exception("Not yet implemented: 2 ways to get from block a to block b")
}



import com.google.gson.Gson
import factorioBlueprint.Entity
import factorioBlueprint.Position
import factorioBlueprint.ResultBP
import graph.Graf
import graph.tiernan
import java.nio.file.Files
import java.nio.file.Path
import java.util.*
import kotlin.collections.ArrayList


fun main(args: Array<String>) {

    val options = args.filter { it.startsWith("-") }
    setCLIOptions(options)
    dbgPrintln("Program arguments: ${args.joinToString()}")
    val inputBlueprintString = args.filter { it.startsWith("0") }
    val BlueprintFile = args.filter { !it.startsWith("-") }
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
    } else {
        if (BlueprintFile.isNotEmpty()) {
            if (Files.exists(Path.of(BlueprintFile.first()))) {
                jsonString = decodeBpStringFromFilename(BlueprintFile.first())

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
    factorioRailAnalyzer(jsonString)

}

fun factorioRailAnalyzer(blueprint: String): Boolean {
    //region Phase0: data decompression
    if (blueprint.contains("blueprint_book")) {
        throw Exception("Sorry, a Blueprintbook cannot be parsed by this Programm, please input only Blueprints ")
    }
    val resultBP: ResultBP = Gson().fromJson(blueprint, ResultBP::class.java)
    val entityList = resultBP.blueprint.entities
    //endregion
    //region Phase1: data cleansing and preparation
    //filter out entity's we don't care about
    //ordered by (guessed) amount they appear in a BP
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
    if (entityList.size < 100) Graphviz().generateEntityRelations(entityList)
    //region Phase3: create
    //prepare
    val signalList: List<Entity> = entityList.filter { entity ->
        entity.isSignal()
    }


    if (signalList.isEmpty()) throw Exception("No Edges Found, because there are no signals in the blueprint") //todo: construction error

    //create forward facing edgedges
    var listOfEdges = arrayListOf<Edge>()
    val relation = mutableMapOf<Int, ArrayList<Edge>>()
    signalList.forEach { startPoint ->
        val resultEdges = arrayListOf<Edge>()
        if (startPoint.rightNextRail.size > 0) resultEdges.addAll(buildEdge(Edge(startPoint), 1, false))
        if (startPoint.leftNextRail.size > 0) resultEdges.addAll(buildEdge(Edge(startPoint), -1, false))

        //remove duplicates
        // this is aproblem of core arichtechture and depredly neads a rework
        // but bc my partner dosent do alot i am runing out of time to fix
        for (i in resultEdges)
            if (listOfEdges.addUnique(i)) //returns true if added
                relation[startPoint.entityNumber!!] = resultEdges


    }
    listOfEdges.countDuplicates()

// get all start signals
    val notStartSignalList = listOfEdges.map { edge ->
        edge.last(1)
    }.toMutableList()
    notStartSignalList.removeAll {
        it.entityType == EntityType.VirtualSignal
    }
    val startSignals = signalList.toSet() - notStartSignalList.toSet()
// do a backwards seach
    var backwardsEdges = arrayListOf<Edge>()
    startSignals.forEach { startPoint ->
        val resultEdges = arrayListOf<Edge>()
        if (startPoint.rightNextRail.size > 0) resultEdges.addAll(buildEdge(Edge(startPoint), -1, true))
        if (startPoint.leftNextRail.size > 0) resultEdges.addAll(buildEdge(Edge(startPoint), 1, true))
        if (resultEdges.size > 0) {
            backwardsEdges.addAll(resultEdges)
        }
    }
    backwardsEdges.forEach { edge ->

        edge.entityList.reverse()
        edge.entityList.first().entityType = EntityType.Signal

    }
    listOfEdges.addAll(backwardsEdges)

    listOfEdges.forEach { it.cleanAndCalc() }
    listOfEdges = listOfEdges.distinct() as ArrayList<Edge>

    // guard check point
    if (listOfEdges.size == 0) {
        throw Exception("No Edges Found, but there are some signals ")  //todo: unexpected  error
    }
    //set next posible edges per edge
    listOfEdges.filter { edge ->
        edge.last(1).entityType != EntityType.VirtualSignal && edge.validRail
    }.forEach { edge ->
        val endingSignal = edge.last(1)
        edge.nextEdgeList = relation[endingSignal.entityNumber]
    }
    backwardsEdges.forEach { it.setDanger() }


    //endregion

    //region Phase4: creating the blocks that are defined by the signals in factorio

    val blockList = connectEdgesToBlocks(listOfEdges)
    var pos = simpleCheck(listOfEdges)
    //pos = entityList.find { it.entityNumber ==20 }?.position ?: Position(0.0,0.0)
    svgFromPoints(max, listOfEdges, signalList, pos)

    // creating the Graph out of the Blocks and edges


    listOfEdges.filter { edge ->
        (edge.belongsToBlock?.istjemandrarwIchBinGefärlich() == true
                || edge.hasRailSignal())
                && edge.validRail
    }.forEach { edge ->
        edge.setzteBeobachtendeEdges()
    }

    listOfEdges.forEach { edge ->
        edge.wasIchBeobachte.forEach {
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


    println("tmTm:")
/*
    val a = listOfEdges.tiernanWithref({
        it.wasIchBeobachte
    }, {
        it.belongsToBlock!!
    })


    println(a)
    val b = listOfEdges.tiernan {
        it.nextEdgeListAL()
    }
    println(b)
    */

    val c = blockList.tiernan {
        it.directNeighbours()
    }.reduceBasic().anylasis()
    val d = blockList.tiernan {
        it.dependingOn
    }



    println(c)
    println(d)

    blockList.visualize("neighborBlocks", c) { it.directNeighbours() }
    blockList.visualize("blockDependency", d) { it.dependingOn }
    listOfEdges.visualizeWithRef("wasIchBeobachte", { it.wasIchBeobachte }) { it.belongsToBlock }
    //listOfEdges.visualizeWithRef("nextEdgeListAL"){it.nextEdgeListAL()}
    //blockList.visualizeWithRef("neighbourBlocks"){it.neighbourBlocks()}
    // Edit here to Test different systems
    return /*a.hasCircutes() || b.hasCircutes()  ||*/ c.hasCircutes()
}

private fun ArrayList<Edge>.countDuplicates() {
    println("dupes:")
    for (i in this.indices)
        for (j in i + 1 until this.size)
            if (this[i].toString() == this[j].toString()) {
                println("index $i und $j sind gleich: ${this[i]}")
                if(this[i] != this[j]) {
                    println(this[i].entityList)
                    println(this[j].entityList)
                    println("dif${this[i].entityList.toSet() subtract  this[j].entityList.toSet()}")
                }
            }

}

private fun Graf<Block>.anylasis(): Graf<Block> {

    this.circularDependencies.retainAll {
        it.analysis()
    }
    return this
}

private fun List<Block>.analysis(): Boolean {

    var transEdge = ArrayList<Set<Edge>>()
    for (i in this.indices) {
        transEdge.add(intersect(this[i], this[(i + 1) % this.size]))
    }
    if (!transEdge.all {
            it.size == 1
        }) {
        transEdge.forEach {
            println(it.size)
        }
        throw Exception("unexpeected")
    }
    //Collections.rotate(transEdge, 1) // this alinges the block list wih th edge lsit
    var trans: MutableList<Edge> = transEdge.flatten().toMutableList()

    var pathIndex = ArrayList<Int>()
    for (i in trans.indices) {
        if (trans[i].nextEdgeList!!.contains(trans[(i + 1) % trans.size])) {
            pathIndex.add(i)
        }
    }
    if (pathIndex.size == 0) return true // if everything is one continues path then this is conlusive
    // this is a konequense that priusely all dls without any rail signals where filterd
    Collections.rotate(trans, -1 * pathIndex[0])
    for (i in pathIndex.indices) {
        var lastOfPrevius = trans[(pathIndex[i] + i - 1) % trans.size]
        var curentStart = trans[pathIndex[i] + i]
        var newStart =
            (lastOfPrevius.belongsToBlock!!.edgeList.toSet()
                    subtract setOf(lastOfPrevius)).toList().first {
                it.nextEdgeList?.contains(curentStart) == true
            }
        trans.add(pathIndex[i] + i, newStart)
    }
    for (i in pathIndex.indices)
        if (trans.subList(pathIndex[i] + i, pathIndex[i + 1] + i + 1).all {
                it.entityList.first().entityType != EntityType.Signal
            })
            return false
    return true
}

fun intersect(from: Block, to: Block): Set<Edge> {
    var von: Set<Edge> = from.edgeList.flatMap {
        it.nextEdgeList!! // todo: this is not save
    }.toSet()
    var nach: Set<Edge> = to.edgeList.toSet()

    return von intersect nach
}


fun simpleCheck(listOfEdges: ArrayList<Edge>): Position {
    if (!listOfEdges.filter { edge ->
            edge.last(1).entityType != EntityType.VirtualSignal
        }.any { edge ->
            edge.hasRailSignal()
        }) {
        //all signals are chain signals with exception to ending signals
        // this guaranties no deadlock
        // return ""
        println("simple check concluded: no deadlock")
    } else {
        println("simple check concluded: potential deadlock")
        var minSize = listOfEdges.filter { edge ->
            edge.hasRailSignal() && edge.last(1).entityType != EntityType.VirtualSignal
        }.minBy { edge ->
            edge.tileLength
        }
        println("max tile length: ${minSize.tileLength} or ${Math.floor((minSize.tileLength + 1) / 7)} train cars")
        return minSize.entityList.first().position
    }
    return Position(-1.0, -1.0)
}

import Clases.Block
import Clases.Edge
import Clases.EntityType
import FRA.NEWfactorioRailAnalyzer
import graph.Graph
import java.nio.file.Files
import java.nio.file.Path
import java.util.*

fun main(args: Array<String>) {


    val options = args.filter { it.startsWith("-") }
    if (args.isEmpty()){
        showCLIHelp()
    }
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
    for (i in indices) {
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



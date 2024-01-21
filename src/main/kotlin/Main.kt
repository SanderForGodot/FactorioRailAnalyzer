
import com.google.gson.Gson
import factorioBlueprint.Entity
import factorioBlueprint.Position
import factorioBlueprint.ResultBP
import java.io.ByteArrayOutputStream
import java.io.File
import java.util.*
import java.util.zip.Inflater
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.pow
import kotlin.math.sqrt


//some idea what this code does, but we didn't write it
//Made by @marcouberti on github
//Source: https://gist.github.com/marcouberti/40dbbd836562b35ace7fb2c627b0f34f
fun ByteArray.zlibDecompress(): String {
    val inflater = Inflater()
    val outputStream = ByteArrayOutputStream()

    return outputStream.use {
        val buffer = ByteArray(1024)

        inflater.setInput(this)

        var count = -1
        while (count != 0) {
            count = inflater.inflate(buffer)
            outputStream.write(buffer, 0, count)
        }

        inflater.end()
        outputStream.toString("UTF-8")
    }
}

fun decodeBpSting(filename: String): String {
    val base64String: String = File(filename).readText(Charsets.UTF_8)
    val decoded = Base64.getDecoder().decode(base64String.substring(1))
    val str: String = decoded.zlibDecompress()
    println(str)
    return str
}

fun main(args: Array<String>) {

    println("Program arguments: ${args.joinToString()}")


    val jsonString: String = decodeBpSting("decodeTest.txt")
    val gson = Gson()
    val resultBP = gson.fromJson(jsonString, ResultBP::class.java)
    println(resultBP)


    //region filter relevant items and determinant min and max of BP
    val relevantEntity = arrayOf(
        "straight-rail",
        "rail-chain-signal",
        "rail-signal",
        "curved-rail"
    ) // ordered in guessed amount they appear in a BP
    var min = Position(0.0, 0.0)
    var max = Position(0.0, 0.0)
    var firstRelevant = true
    val entityIter = resultBP.blueprint.entities.iterator()

    while (entityIter.hasNext()) {
        val entity = entityIter.next()

        if (!relevantEntity.contains(entity.name)) {
            entityIter.remove()
        }
        if (firstRelevant) {
            min = entity.position.copy()
            max = entity.position.copy()
            firstRelevant = false
        } else {
            val current = entity.position
            if (min.x > current.x)
                min.x = current.x
            if (min.y > current.y)
                min.y = current.y
            if (max.x < current.x)
                max.x = current.x
            if (max.y < current.y)
                max.y = current.y
        }
    }
    //endregion
    //region transform coordinate space to start at 1, 1 (this makes the top left rail-corner be at 0.0)
    min.x = floor(min.x / 2) * 2
    min.y = floor(min.y / 2) * 2
    max.x -= min.x
    max.y -= min.y
    println(min)
    println(max)
    resultBP.blueprint.entities.forEach { entity ->
        entity.position.x -= min.x
        entity.position.y -= min.y
    }
    //  println(resultBP.blueprint.entities)

    val matrix: Array<Array<ArrayList<Entity>?>> =
        Array(ceil(max.x / 2).toInt()) {
            Array(ceil(max.y / 2).toInt()) {
                null
            }
        }


    //endregion
    //region insert entity's into 2D Array for essayer look up in the next step
    resultBP.blueprint.entities.forEach { entity ->
        entity.ini()
        val x = floor(entity.position.x / 2).toInt()
        val y = floor(entity.position.y / 2).toInt()
        if (matrix[x][y] == null)
            matrix[x][y] = arrayListOf(entity)
        else
            matrix[x][y]!!.add(entity)
    }
    //endregion
    //region rail Linker: connected rails point to each other with pointer list does the same with signals
    val graphviz = Graphviz() //for the grafikal output in graphviz
    graphviz.startGraph()
    val listOfSignals: ArrayList<Entity> = arrayListOf()
    resultBP.blueprint.entities.forEach outer@{ entity ->
        if (entity.name == "rail-signal" || entity.name == "rail-chain-signal") {
            listOfSignals.add(entity)
            return@outer
        }
        fact[entity.name]?.get(entity.direction)?.forEach inner@{ possibleRail ->
            val possiblePosition = entity.position + possibleRail.position
            val x = floor(possiblePosition.x / 2).toInt()
            val y = floor(possiblePosition.y / 2).toInt()
            if (x < 0 || y < 0 || matrix.size <= x || matrix[0].size <= y) {
                return@inner
            }
            matrix[x][y]?.forEach { foundRail ->
                if (foundRail.name.contains(possibleRail.name)
                    && foundRail.direction == possibleRail.direction
                ) {
                    if (possibleRail.name == "signal") {
                        if (possibleRail.entityNumber == 1) {
                            foundRail.rightNextRail.addUnique(entity)
                            entity.signalOntheRight.addUnique(foundRail)
                        } else {
                            foundRail.leftNextRail.addUnique(entity)
                            entity.signalOntheLeft.addUnique(foundRail)
                        }
                    } else {
                        if (possibleRail.entityNumber == 1) {//right

                            entity.rightNextRail.addUnique(foundRail)

                        } else {

                            entity.leftNextRail.addUnique(foundRail)

                        }
                    }
                }
            }
        }
        println(entity.relevantShit())
        graphviz.appendEntity(entity)
    }
    graphviz.endGraph()
    graphviz.createoutput()

    //endregion
    val listOfEdges: ArrayList<Edge> = arrayListOf()

    listOfSignals.forEach { startPoint ->
        val test = buildEdge(Edge(startPoint), if (startPoint.direction < 4) -1 else 1)
        listOfEdges.addAll(test)
    }
    var i =0;
    listOfEdges.forEach {
        println(it)
        printEdge(it,i)
        i++
    }
}


/*
entity  = the curent entity we are in to build an edge
direction  =  -1 curently moving levt
               +1 curently moving right
               filps at strait up rails
 */

fun <E> ArrayList<E>.addUnique(element: E) {
    if (!this.contains(element))
        this.add(element)
    else
    //throw Exception("Item already in list")
        println("irgnored error")
}


fun buildEdge(edge: Edge, direction: Int): ArrayList<Edge> {

    if (edge.last(1).hasSignal()) {
        val end = determineEnding(edge, direction)
        if (end != null)
            return arrayListOf(end)
        //otherwise continue and ignore (happens once at the start of every edg to ignore the starting signal)
        /*
                if (wrongSide != null) {
                    if (goodSide != null) {
                        val signalCount = goodSide.size + wrongSide.size
                        when (signalCount) {//1 impossible since there is one signal in each list good and wrong side
                            2 -> {
                                if (isSignalOpposite(goodSide[0], wrongSide[0])) {
                                    //found edge successfully
                                    return arrayListOf(Edge(edge, goodSide[0]))
                                } else {
                                    //illegal rail discard
                                    return null;
                                }
                            }

                            3 -> {
                                if (goodSide.size == 2) {//found edge successfully
                                    return arrayListOf(Edge(edge, signal_is_closer(edge.last(), goodSide[0], goodSide[1])))
                                } else {
                                    //illegal rail discard
                                    return null;
                                }
                            }

                            4 -> return arrayListOf(
                                Edge(
                                    edge,
                                    signal_is_closer(edge.last(), goodSide[0], goodSide[1])
                                )
                            )    // TODO: Check if this works, or if you need the rail BEFORE edge.last()
                        }

                    } else {
                        //illegal rail discard
                        return null;
                    }
                } else if (goodSide!!.size > 1) {
                    // There are two signals on the rail, we need to find the first signal and close the edge on that signal
                    // TODO: Check if this works, or if you need the rail BEFORE edge.last()
                    return arrayListOf(Edge(edge, signal_is_closer(edge.last(), goodSide[0], goodSide[1])))
                } else {
                    //found edge successfully
                    return arrayListOf(Edge(edge, goodSide[0]))
                }
                */
    }
    val arr: ArrayList<Edge> = arrayListOf()
    val nextRails = edge.last(1).getDirectionalRailList(direction)
    if (nextRails.size>0)
    nextRails.forEach { entity ->
        val modifier = isSpecialCase(edge.last(1), entity)
        val result = buildEdge(Edge(edge, entity), direction * modifier)
        arr.addAll(result)
    } else {
        val blankSignal = Entity(0, "blank-Signal")
        arr.add(edge.finishUpEdge(blankSignal, true))
    }

    return arr
}

fun determineEnding(edge: Edge, direction: Int): Edge? {
    //this is an edge case fest
    //we need to check if the signals are relevant and if so is they are on the correct side or at least have a partner

    val goodSide = edge.last(1).getDirectionalSignalList(direction)?.clone() as ArrayList<Entity>?
    val wrongSide = edge.last(1).getDirectionalSignalList(-direction)?.clone() as ArrayList<Entity>?
    while (goodSide?.contains(edge.EntityList.first()) == true) { //remove the starting node so that rail signals end themselves
        goodSide.remove(edge.EntityList.first()) //todo re write this funkktion
    }
    val hasWrong: Boolean = wrongSide?.isNotEmpty() ?: false // if there a signal on the opposite side we asume problems
    val anzRight = if (goodSide?.size == null) 0 else goodSide.size // I am proud of this line


    when {
        hasWrong && anzRight == 0 -> {
            val endSignal = getClosetSignal(edge, wrongSide) ?: throw Exception()//impossible case
            return edge.finishUpEdge(endSignal, false)
        }
        hasWrong && anzRight == 1 -> {
            var isOpposite = isSignalOpposite(goodSide!![0], wrongSide!![0]) //!! ist save
            when (wrongSide.size) {
                1 -> {
                    val closestSignal = getClosesdSignal(edge.last(2), goodSide[0], wrongSide[0])
                    return edge.finishUpEdge(if (isOpposite) goodSide[0] else closestSignal, isOpposite)
                }
                2 -> {//one good signal and 2 bad
                    val closestWrong: Entity = getClosesdSignal(edge.last(2), wrongSide[0], wrongSide[1])
                    isOpposite = isSignalOpposite(goodSide[0], closestWrong)
                    return edge.finishUpEdge(if (isOpposite) goodSide[0] else closestWrong, isOpposite)
                }
                else -> {
                    //should be impossible
                    /*bc:
                    * isWrong== true -> isWrong.size>0 -> 0 in impossible
                    * a rail can only have 2 signals on one side -> more than 3 is impossible
                    * */
                    throw Exception("Impossible ")
                }
            }
        }

        !hasWrong && anzRight == 0 -> {
            assert(edge.EntityList.size < 2)
            return null
        }//Start case, first signal was filtered out
        !hasWrong && anzRight == 1 -> {
            return edge.finishUpEdge(goodSide!![0], true)
        }
        anzRight == 2 -> {
            return edge.finishUpEdge(getClosetSignal(edge, goodSide)!!, true)
        }
        else -> throw Exception()
    }
}

fun isSpecialCase(current: Entity, next: Entity): Int {
    val candidates = intArrayOf(0, 1, 4, 5)
    // sorts outs most cases to improve efficiency
    if (!candidates.contains(current.direction) || !candidates.contains(next.direction))
        return 1

    if (current.name == "curved-rail" && current.direction == 0)
        if (next.name == "straight-rail" && next.direction == 0 ||
            next.name == "curved-rail" && next.direction == 5
        )
            return -1
    if (current.name == "straight-rail" && current.direction == 0)
        if (next.name == "curved-rail" && next.direction == 0)
            return -1
    if (current.name == "curved-rail" && current.direction == 5)
        if (next.name == "curved-rail" && next.direction == 0)
            return -1

    if (current.name == "curved-rail" && current.direction == 4)
        if (next.name == "straight-rail" && next.direction == 0 ||
            next.name == "curved-rail" && next.direction == 1
        )
            return -1
    if (current.name == "straight-rail" && current.direction == 0)
        if (next.name == "curved-rail" && next.direction == 4)
            return -1
    if (current.name == "curved-rail" && current.direction == 1)
        if (next.name == "curved-rail" && next.direction == 4)
            return -1

    return 1

}


/*

        if (it.rightNextRail != null && !it.rightNextRail?.contains(startPoint)!!) {
                recusion(Edge(startPoint))
            }

* */
/*

fun recusion_alt(edge: Edge) {
    var item = edge.EntityList.last()

    //  var corectdirection =

    leftBranch(item, edge)
    item.rightNextRail?.forEach { rightNext ->
        if (rightNext.leftNextRail!!.contains(item)) {
            //wrong way go oposite
        }
    }


}

fun leftBranch(item: Entity, edge: Edge) {
    if (item.signalOntheRight != null) {
        //if rail on oposide left
        //end edge sucsesfuly
        //else
        //invalid edge
    }
    if (item.signalOntheLeft != null) {
        if (item.signalOntheLeft!!.size == 1) {
            // end edge sucsesfuly
        } else {
            // get closes
        }

    }


    item.leftNextRail?.forEach { leftNext ->
        if (leftNext.rightNextRail!!.contains(item)) {
            var newEdge = Edge(edge)
            newEdge.EntityList.add(leftNext)
            recusion_alt(newEdge)
        } else {

            //wenn das if einmal scheitert kann die ganze liste gespipt werden
            return;
        }
    }
}

fun rightBranch() {

}
*/

fun isSignalOpposite(signal1: Entity, signal2: Entity): Boolean {
    val distanceSignal = distanceOfEntitys(signal1, signal2)
    return (distanceSignal <= 3) //TODO: Check the minimum distance so that the signal is opposite, maybe different distances for straight and curved
}


fun getClosetSignal(
    edge: Edge,
    signals: ArrayList<Entity>?
): Entity? {
    if (signals == null) return null
    when (signals.size) {
        0 -> return null
        1 -> return signals[0]
        2 -> {
             
           /* val rail1 =
                if ((signals[0].rightNextRail[0]) == null)
                    signals[0].leftNextRail[0]
                else
                    signals[0].rightNextRail[0]
            val rail2 =
                if ((signals[1].rightNextRail[0]) == null)
                    signals[1].leftNextRail[0]
                else signals[1].rightNextRail[0]
            assert(rail1 == rail2)*/
            return getClosesdSignal(edge.last(2), signals[0], signals[1])
        }
    }
    return null
}

fun getClosesdSignal(
    rail: Entity,
    signal1: Entity,
    signal2: Entity
): Entity { //Important: This needs the rail BEFORE the rail with the signal otherwise it has undefined behavior
    val distanceSignal1 = distanceOfEntitys(rail, signal1)
    val distanceSignal2 = distanceOfEntitys(rail, signal2)
    return if (distanceSignal1 < distanceSignal2) signal1 else signal2
}

fun distanceOfEntitys(entity1: Entity, entity2: Entity): Double {
    val yDifference = (entity1.position.y - entity2.position.y).pow(2)
    val xDifference = (entity1.position.x - entity2.position.x).pow(2)
    return sqrt((yDifference + xDifference))
}

fun printEdge(edge: Edge,i:Int){
    val graphviz = Graphviz()
    val stringBuilder = StringBuilder()

    graphviz.format(stringBuilder,edge)
    //println(stringBuilder.toString())
    File("input.dot").writeText(stringBuilder.toString())
    val result = ProcessBuilder("dot","-Tsvg","input.dot", "-o output$i.svg")
        .redirectOutput(ProcessBuilder.Redirect.INHERIT)
        .redirectError(ProcessBuilder.Redirect.INHERIT)
        .start()
        .waitFor()
}
















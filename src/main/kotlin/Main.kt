import com.google.gson.Gson
import factorioBlueprint.Entity
import java.util.*
import kotlin.collections.ArrayList

import factorioBlueprint.*
import java.io.ByteArrayOutputStream

import java.io.File

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
    var resultBP = gson.fromJson(jsonString, ResultBP::class.java)
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
    var firstRelevant = true;
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
            val current = entity.position;
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

    var matrix: Array<Array<ArrayList<Entity>?>>
    matrix = Array(ceil(max.x / 2).toInt()) { row ->
        Array(ceil(max.y / 2).toInt()) { col ->
            null
        }
    }

    var signals: ArrayList<Entity> = arrayListOf();

    //endregion
    //region insert entity's into 2D Array for essayer look up in the next step
    resultBP.blueprint.entities.forEach { entity ->
        val x = floor(entity.position.x / 2).toInt()
        val y = floor(entity.position.y / 2).toInt()
        if (matrix[x][y] == null)
            matrix[x][y] = arrayListOf(entity)
        else
            matrix[x][y]!!.add(entity)
        Entity(0, "straight-rail", Position(0.0, -2.0), 0)
        Entity(0, "curved-rail", Position(3.0, -3.0), 5)


    }
    //endregion
    //region rail Linker: connected rails point to each other with pointer list does the same with signals
    var listOfSignals: ArrayList<Entity> = arrayListOf();
    resultBP.blueprint.entities.forEach outer@{ entity ->
        if (entity.name == "rail-signal" || entity.name == "rail-chain-signal") {
            listOfSignals.add(entity)
            return@outer
        }
        fact[entity.name]?.get(entity.direction)?.forEach inner@{ possibleRail ->
            var possiblePosition = entity.position + possibleRail.position
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
                            foundRail.rightNextRail.createOrAdd(entity)
                            entity.signalOntheRight.createOrAdd(foundRail)
                        } else {
                            foundRail.leftNextRail.createOrAdd(entity)
                            entity.signalOntheLeft.createOrAdd(foundRail)
                        }
                    } else {
                        if (possibleRail.entityNumber == 1) {//right

                            entity.rightNextRail.createOrAdd(foundRail)

                        } else {

                            entity.leftNextRail.createOrAdd(foundRail)

                        }
                    }
                }
            }
        }
    }


    //endregion
    val listOfEdges: ArrayList<Edge> = arrayListOf<Edge>()

    listOfSignals.forEach { startPoint ->
      buildEdge(Edge(startPoint), if (startPoint.direction < 4) -1 else 1)?.let { listOfEdges.addAll(it) }
    }
    listOfEdges.forEach {
        println(it)
    }
}


/*
entity  = the curent entity we are in to build an edge
direction  =  -1 curently moving levt
               +1 curently moving right
               filps at strait up rails
 */
fun buildEdge(edge: Edge, direction: Int): ArrayList<Edge>? {

    if (edge.last().hasSignal()) {
        return determineEnding(edge,direction)
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
    val arr: ArrayList<Edge> = arrayListOf<Edge>()
    edge.last().getDirectionalRailList(direction)?.forEach { entity ->
        val modifier = isSpecialCase(edge.last(), entity)
        buildEdge(Edge(edge, entity), direction * modifier)?.let { arr.addAll(it) }
    } ?: {
        val blankSignal = Entity(0, "blank-Signal")
        arr.add(edge.finishUpEdge(blankSignal,true))
    }

    return arr;
}
fun determineEnding(edge: Edge, direction:Int): ArrayList<Edge>
{
    val goodSide = edge.last().getDirectionalSignalList(direction)
    val wrongSide = edge.last().getDirectionalSignalList(-direction)
    if (goodSide?.contains(edge.EntityList.first()) == true) { //remove the staring node so that rail signals end themselves
        goodSide.remove(edge.EntityList.first())
    }
    val isWrong = wrongSide != null; // is there a signal on the oposite side we asume problems
    val anzRight = if (goodSide?.size == null) 0 else goodSide.size // I am proud of this line


    when {
        isWrong && anzRight == 0 -> {
            val endSignal = getClosetSignal(goodSide)
            if (endSignal == null) {
                //impossible case
                println("VERY BAD ")
                println("VERY BAD ")
                println("VERY BAD ")
                println("idk how to throw debug statements")
                //wir müsten hier braken zum ende von if das dies ein fals prositf wäre
                throw Exception()
            }
            return arrayListOf(edge.finishUpEdge(endSignal, false))
        }

        isWrong && anzRight == 1 -> {
            var isOpposite = isSignalOpposite(goodSide!![0], wrongSide!![0]) //!! ist save
            when (wrongSide.size) {
                1 -> return arrayListOf(edge.finishUpEdge(goodSide[0], isOpposite))
                2 -> {//one good signal and 2 bad

                    val closestWrong: Entity = signal_is_closer(edge.secondLast(),wrongSide[0], wrongSide[1])
                    isOpposite = isSignalOpposite(goodSide[0], closestWrong)

                    return arrayListOf(edge.finishUpEdge(if (isOpposite)goodSide[0]else closestWrong, isOpposite))
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

        !isWrong && anzRight == 0 -> {}
        !isWrong && anzRight == 1 -> {
            return arrayListOf(edge.finishUpEdge(goodSide!![0], true))
        }

        anzRight == 2 -> {
            return arrayListOf(edge.finishUpEdge(getClosetSignal(goodSide)!!, true))
        }

        else -> throw Exception()
    }
    throw Exception()
}

fun isSpecialCase(current: Entity, next: Entity): Int {
    val candidates = intArrayOf(0, 1, 4, 5)
    // sorts outs most cases to improve efficiency
    if (!candidates.contains(current.direction) || !candidates.contains(next.direction))
        return 1;

    if (current.name == "curved-rail" && current.direction == 0)
        if (next.name == "straight-rail" && next.direction == 0 ||
            next.name == "curved-rail" && next.direction == 5
        )
            return -1;
    if (current.name == "straight-rail" && current.direction == 0)
        if (next.name == "curved-rail" && next.direction == 0)
            return -1;
    if (current.name == "curved-rail" && current.direction == 5)
        if (next.name == "curved-rail" && next.direction == 0)
            return -1;

    if (current.name == "curved-rail" && current.direction == 4)
        if (next.name == "straight-rail" && next.direction == 0 ||
            next.name == "curved-rail" && next.direction == 1
        )
            return -1;
    if (current.name == "straight-rail" && current.direction == 0)
        if (next.name == "curved-rail" && next.direction == 4)
            return -1;
    if (current.name == "curved-rail" && current.direction == 1)
        if (next.name == "curved-rail" && next.direction == 4)
            return -1;

    return 1;

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

fun <E> ArrayList<E>?.createOrAdd(item: E): ArrayList<E> {
    var list = this
    if (list == null)
        list = arrayListOf(item)
    else
        list.add(item)
    list.add(item)
    return list;
}

fun isSignalOpposite(signal1: Entity, signal2: Entity): Boolean {
    val distanceSignal = distanceOfEntitys(signal1, signal2)
    return (distanceSignal <= 3) //TODO: Check the minimum distance so that the signal is opposite, maybe different distances for straight and curved
}


fun getClosetSignal(signals: ArrayList<Entity>?): Entity? {
    if (signals == null) return null;
    when (signals.size) {
        0 -> return null;
        1 -> return signals[0]
        2 -> {
            val rail1 =
                if ((signals[0].rightNextRail?.get(0)) == null) signals[0].leftNextRail?.get(0) else signals[0].rightNextRail?.get(
                    0
                )
            val rail2 =
                if ((signals[1].rightNextRail?.get(0)) == null) signals[1].leftNextRail?.get(0) else signals[1].rightNextRail?.get(
                    0
                )
            assert(rail1 == rail2)
            return signal_is_closer(rail1!!, signals[0], signals[1])
        }
    }
    return null;
}

fun signal_is_closer(rail: Entity, signal1: Entity, signal2: Entity): Entity {
    val distanceSignal1 = distanceOfEntitys(rail, signal1)
    val distanceSignal2 = distanceOfEntitys(rail, signal2)
    return if (distanceSignal1 < distanceSignal2) signal1 else signal2;
}

fun distanceOfEntitys(entity1: Entity, entity2: Entity): Double {
    val yDifference = (entity1.position.y - entity2.position.y).pow(2)
    val xDifference = (entity1.position.x - entity2.position.x).pow(2)
    return sqrt((yDifference + xDifference))
}

















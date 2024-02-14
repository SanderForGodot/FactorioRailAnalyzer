import com.google.gson.Gson
import factorioBlueprint.Entity
import factorioBlueprint.Position
import factorioBlueprint.ResultBP
import graph.Graph
import kotlin.math.ceil
import kotlin.math.floor

fun main(args: Array<String>) {

    println("Program arguments: ${args.joinToString()}")

    //region Phase0: data decompression
    val jsonString: String = decodeBpSting("decodeTest.txt")
    val resultBP = Gson().fromJson(jsonString, ResultBP::class.java)
    val entityList = resultBP.blueprint.entities
    //endregion

    //region Phase1: data cleansing and preparation
    //filter out entitys we don't care about
    //ordered by (guessed) amount they appear in a BP
    entityList.retainAll {
        it.entityType != EntityType.Error
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
    val graphviz = Graphviz() //for the grafikal output in graphviz
    graphviz.startGraph()

    entityList.railLinker(matrix)

    val listOfSignals: ArrayList<Entity> = entityList.filter { entity ->
        entity.isSignal()
    } as ArrayList<Entity>


    //region colps
    entityList.forEach { entity ->
        println(entity.relevantShit())
        graphviz.appendEntity(entity)
    }

    graphviz.endGraph()
    graphviz.createoutput()

    var relation = mutableMapOf<Entity, ArrayList<Edge>>()
    listOfSignals.forEach { startPoint ->
        relation[startPoint] = buildEdge(Edge(startPoint), if (startPoint.direction < 4) -1 else 1)
    }

    val listOfEdges = arrayListOf<Edge>()
    relation.values.forEach { edgeList ->
        listOfEdges.addAll(edgeList)
    }
    var hasPartnerSignal = arrayListOf<Entity>()

    listOfEdges.forEach { edge ->
        val signal = edge.last(1)
        if (!signal.isSignal())
            throw Exception("Signal oder kein Signal das ist hier die frage")
        if (signal.entityType == EntityType.VirtualSignal)
            return@forEach
        edge.nextEdgeList = relation[signal]!!
        hasPartnerSignal.addUnique(signal)
    }
    var startSignales = listOfSignals.toSet() - hasPartnerSignal.toSet()

    //calculating the lengths of the edges
    listOfEdges.forEach { edge ->
        edge.calcTileLength()
    }
    if (listOfEdges.size == 0) throw Exception("No Edges Found, probably because there are no signals in the blueprint")
    //creating the blocks that are defined by the signals in factorio
    var blockList = arrayListOf<Block>(Block(listOfEdges[0], 0))
    listOfEdges[0].belongsToBlock = blockList[0]

    var counter: Int = 0
    listOfEdges.filter { edge ->
        listOfEdges.first() != edge
    }.forEach { edge ->
        blockList.forEach { block ->
            if (block.doesCollide(edge)) {
                if (edge.belongsToBlock == null) {
                    block.edgeList.add(edge)
                    edge.belongsToBlock = block
                } else {
                    edge.belongsToBlock!!.merge(block)
                    blockList.remove(block)
                }
            }
        }
        if (edge.belongsToBlock == null) {
            counter++
            val newBlock = Block(edge, counter)
            edge.belongsToBlock = newBlock
            blockList.add(newBlock)
        }
    }

    // creating the Graph out of the Blocks and edges
    var graph: MutableMap<Int, MutableList<Int>> = mutableMapOf()
    blockList.filter { block ->
        block.isRelevant(startSignales)
    }.forEach { block ->
        graph[block.id] = block.findEnd().toMutableList()
    }

    //analysing the graph
    val graphTesting = Graph()
    graphTesting.setGraph(graph)
    graphTesting.tiernan()

    //debug output
    var i = 0;
    listOfEdges.forEach {
        println(it)
        printEdge(it, i)
        i++

    }
    //endregion
}

fun ArrayList<Entity>.railLinker(matrix: Array<Array<ArrayList<Entity>?>>) {
    //endregion
    this.filter { entity ->
        entity.isRail()
    }.forEach outer@{ Rail -> // for Each (R)eal rail entity
        fact[Rail.entityType]?.get(Rail.direction)?.forEach inner@{ factEntity -> // for each entity T
            // calulate P = R + T
            val possiblePosition = Rail.position + factEntity.position
            val x = floor(possiblePosition.x / 2).toInt()
            val y = floor(possiblePosition.y / 2).toInt()
            if (x < 0 || y < 0 || matrix.size <= x || matrix[0].size <= y) {
                return@inner
            }
            //look up P in matrix
            addEachMachingEntity(matrix[x][y], factEntity,Rail)
        }
    }
}
//todo ggf nen besseren namen
fun addEachMachingEntity(entities: ArrayList<Entity>?, factEntity: Entity, Rail: Entity)
{
    entities?.filter { entity ->// for each existing Entity E
        // if T and E are equel (exept position)
        (entity.isSignal() == factEntity.isSignal()||
                entity.isRail() == factEntity.isRail())  //thecicly  this line is unecesary
                && entity.direction == factEntity.direction
    }?.forEach { foundEntity ->
        // yes -> Add a reference from R to E to the direction depending on T
        if (foundEntity.isSignal()) {
            // for signal we do tow way add and set an edge case var
            foundEntity.removeRelatedRail = setRemoveRelatedRail(foundEntity, factEntity, Rail)
            foundEntity.getRailList(factEntity.entityNumber!!).addUnique(Rail)
            Rail.getSignalList(factEntity.entityNumber!!).addUnique(foundEntity)
        } else {
            Rail.getRailList(factEntity.entityNumber!!).addUnique(foundEntity)
        }
    }
}
fun setRemoveRelatedRail(foundRail: Entity, factEntity: Entity, Rail: Entity): Boolean {
    //set removeRelatedRail depending on foundRail (A) and possibleRail (input)
    var current = foundRail.removeRelatedRail
    var factVal = factEntity.removeRelatedRail!! //theoretical value determiand by the fackt tk
    return when (current) {
        // if bool is not set thake the input value
        null -> factVal
        // if the bool and the iput have the same value all ok contine
        (factVal == current) -> current
        // if the rails disagree prioritise the curved rail state
        else -> (Rail.entityType == EntityType.CurvedRail) == factVal

    }
}


//region Phase1 funktions

fun ArrayList<Entity>.determineMinMax(): Pair<Position, Position> {
    val min = this.first().position.copy()
    val max = min.copy()
    this.forEach { entity ->

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
    // round down min value to be certain that every rail is included
    min.x = floor(min.x / 2) * 2
    min.y = floor(min.y / 2) * 2

    return Pair(min, max)
}

fun generateMatrix(size: Position): Array<Array<ArrayList<Entity>?>> {
    // the cordinate space is comprest by 2 to reduce the amount of empty List, as the Rails are on a 2 by 2 cordinate space anyway
    return Array(ceil(size.x / 2).toInt() + 1) {
        Array(ceil(size.y / 2).toInt() + 1) {
            null
        }
    }
}

fun ArrayList<Entity>.filedMatrix(size: Position): Array<Array<ArrayList<Entity>?>> {
    if (size.x < 8) {
        size.x = 8.0
    }// make size at least 8 big, so that the matrix is at least 4 big, since a curved rail has the position 4
    if (size.y < 8) {
        size.y = 8.0
    }
    val matrix = generateMatrix(size)
    // insert entity's into 2D Array based on the x y coordinates of the entity
    this.forEach { entity ->
        entity.ini()
        // calculate target x y base on the squashed system
        val x = floor(entity.position.x / 2).toInt()
        val y = floor(entity.position.y / 2).toInt()
        if (matrix[x][y] == null)
            matrix[x][y] = arrayListOf(entity)
        else
            matrix[x][y]!!.add(entity)
    }
    return matrix
}
//endregion

fun buildEdge(edge: Edge, direction: Int): ArrayList<Edge> {

    if (edge.last(1).hasSignal()) {
        val end = determineEnding(edge, direction)
        if (end != null)
            return arrayListOf(end)
        //otherwise continue and ignore (happens once at the start of every edg to ignore the starting signal)
    }
    val arr: ArrayList<Edge> = arrayListOf()
    val nextRails = edge.last(1).getRailList(direction)
    if (nextRails.size > 0)
        nextRails.forEach { entity ->
            val modifier = isSpecialCase(edge.last(1), entity)
            val result = buildEdge(Edge(edge, entity), direction * modifier)
            arr.addAll(result)
        } else {
        val blankSignal = Entity(0, EntityType.VirtualSignal, Position(0.0, 0.0), 123, true)
        blankSignal.entityType = EntityType.VirtualSignal
        arr.add(edge.finishUpEdge(blankSignal, true))
    }

    return arr
}

fun determineEnding(edge: Edge, direction: Int): Edge? {
    //this is an edge case fest
    //we need to check if the signals are relevant and if so is they are on the correct side or at least have a partner

    val goodSide = edge.last(1).getSignalList(direction)?.clone() as ArrayList<Entity>?
    val wrongSide = edge.last(1).getSignalList(-direction)?.clone() as ArrayList<Entity>?
    while (goodSide?.contains(edge.EntityList.first()) == true) { //remove the starting node so that rail signals end themselves
        goodSide.remove(edge.EntityList.first()) //todo re write this funkktion
    }
    val hasWrong: Boolean =
        wrongSide?.isNotEmpty() ?: false // if there a signal on the opposite side we asume problems
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
                    val closestSignal = getClosestSignal(edge.last(2), goodSide[0], wrongSide[0])
                    return edge.finishUpEdge(if (isOpposite) goodSide[0] else closestSignal, isOpposite)
                }

                2 -> {//one good signal and 2 bad
                    val closestWrong: Entity = getClosestSignal(edge.last(2), wrongSide[0], wrongSide[1])
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

fun determineEndingNew(edge: Edge, direction: Int): Edge? {

    if (edge.last(1).entityType == EntityType.Rail) {
        return determineEndingForStraightRail(edge, direction)
    } else {
        return determineEndingForCurvedRail(edge, direction)
    }

    return null
}

fun determineEndingForStraightRail(edge: Edge, direction: Int): Edge? {
    return null
}

fun determineEndingForCurvedRail(edge: Edge, direction: Int): Edge? {
    val goodSide = edge.last(1).getSignalList(direction)?.clone() as ArrayList<Entity>?
    val wrongSide = edge.last(1).getSignalList(-direction)?.clone() as ArrayList<Entity>?

    if (goodSide?.any { it == edge.EntityList.first() } == true || wrongSide?.any { it == edge.EntityList.first() } == true)
    //check if the starting signal of the edge is on the rail that called this function
    {
        if (isStartSignalAtEnd(edge)) {
            return null//continue with edge creation
        } else {
            return determineEndingStartSignalAtStartPosition(edge, direction)
        }
    } else {
        return determineEndingTrueEnding(edge, direction)

    }

    return null
}

fun isStartSignalAtEnd(edge: Edge): Boolean { // Todo: test this fucking function, sanders explanation was no help
    val rail = edge.last(1)
    val signal = edge.EntityList.first()

    var virtualsignal = fact[rail.entityType]?.get(rail.direction)?.filter { signal.direction == it.direction }
    if (virtualsignal?.first()?.removeRelatedRail == true) {
        // removeRelatedRail correlates to the position of the signal, which in turn determines if it is a starting or ending position
        // somehow those are the same (blame snader) else build a new lookuptable(leo)
        return false //maybe wrong, other way around
    } else {
        return true //maybe wrong, other way around
    }

}

fun determineEndingStartSignalAtStartPosition(edge: Edge, direction: Int): Edge? {
    val goodSide = edge.last(1).getSignalList(direction)?.clone() as ArrayList<Entity>?
    val wrongSide = edge.last(1).getSignalList(-direction)?.clone() as ArrayList<Entity>?
    val startSignal = edge.EntityList.first()
    val signalCount = (goodSide?.size ?: 0) + (wrongSide?.size ?: 0)

    return when (signalCount) {// different cases for the 4 different possible signal numbers
        1 -> {
            return null
        }//continue with edge creation
        2 -> {
            if (goodSide!!.size == 2) {
                val endSignal = goodSide.first { it != startSignal }
                //end edge with signal on the same side, which is not the starting signal
                return edge.finishUpEdge(endSignal, true)
            }
            if (wrongSide != null) {
                if (isSignalOpposite(startSignal, wrongSide.first())) {
                    return null//continue with edge creation
                } else {
                    val falseSignal = wrongSide.first()
                    // take the signal on the wrong side
                    return edge.finishUpEdge(falseSignal, false)
                }
            } else {
                throw Exception("determine ending got logically impossible case")
            }
        }

        3 -> {
            if ((wrongSide!!.size) == 2) {
                val falseSignal = wrongSide.first { !isSignalOpposite(startSignal, it) }
                // take the signal on the wrong side that is also on the wrong end
                return edge.finishUpEdge(falseSignal, false)
            } else {
                val endsignal = goodSide?.first { it != startSignal }
                //end edge with signal on the same side, which is not the starting signal
                return edge.finishUpEdge(endsignal!!, true)
            }
        }

        4 -> {
            val endsignal = goodSide?.first { it != startSignal }
            //end edge with signal on the same side, which is not the starting signal
            return edge.finishUpEdge(endsignal!!, true)
        }

        else -> {
            throw Exception("determine ending got wrong number of signals:$signalCount")
        }
    }
}

fun determineEndingTrueEnding(edge: Edge, direction: Int): Edge? {
    val goodSide = edge.last(1).getSignalList(direction)?.clone() as ArrayList<Entity>?
    val wrongSide = edge.last(1).getSignalList(-direction)?.clone() as ArrayList<Entity>?

    if ((wrongSide?.size ?: 0) > (goodSide?.size ?: 0)) {
        //not valid rail
        val falseSignal = wrongSide?.first()
        // take the signal on the wrong side
        return edge.finishUpEdge(falseSignal!!, false)
    } else {
        if (((wrongSide?.size ?: 0) == 1) && ((goodSide?.size ?: 0) == 1)) {
            if (!isSignalOpposite(wrongSide!!.first(), goodSide!!.first())) {
                //not valid rail
                val falseSignal = wrongSide.first()
                // take the signal on the wrong side
                return edge.finishUpEdge(falseSignal, false)
            }

        }
    }
    // every other case is a correct edge
    val endSignal = getClosetSignal(edge, goodSide)
    //end edge with signal on the good side
    return endSignal?.let { edge.finishUpEdge(it, true) }
}
//at specific transitions from rail a to b we need to flip the direction indicator

fun isSpecialCase(current: Entity, next: Entity): Int {
    val candidates = intArrayOf(0, 1, 4, 5)
    // sorts outs most cases to improve efficiency
    if (!candidates.contains(current.direction) || !candidates.contains(next.direction))
        return 1


    if (current.entityType == EntityType.CurvedRail && current.direction == 0)
        if (next.entityType == EntityType.Rail && next.direction == 0 ||
            next.entityType == EntityType.CurvedRail && next.direction == 5
        )
            return -1
    if (current.entityType == EntityType.Rail && current.direction == 0)
        if (next.entityType == EntityType.CurvedRail && next.direction == 0)
            return -1
    if (current.entityType == EntityType.CurvedRail && current.direction == 5)
        if (next.entityType == EntityType.CurvedRail && next.direction == 0)
            return -1

    if (current.entityType == EntityType.CurvedRail && current.direction == 4)
        if (next.entityType == EntityType.Rail && next.direction == 0 ||
            next.entityType == EntityType.CurvedRail && next.direction == 1
        )
            return -1
    if (current.entityType == EntityType.Rail && current.direction == 0)
        if (next.entityType == EntityType.CurvedRail && next.direction == 4)
            return -1
    if (current.entityType == EntityType.CurvedRail && current.direction == 1)
        if (next.entityType == EntityType.CurvedRail && next.direction == 4)
            return -1

    return 1

}

fun isSignalOpposite(signal1: Entity, signal2: Entity): Boolean {
    val distanceSignal = signal1.distanceTo(signal2)
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
            return getClosestSignal(edge.last(2), signals[0], signals[1])
        }
    }
    return null
}

fun getClosestSignal(
    rail: Entity,
    signal1: Entity,
    signal2: Entity
): Entity { //Important: This needs the rail BEFORE the rail with the signal otherwise it has undefined behavior
    val distanceSignal1 = rail.distanceTo(signal1)
    val distanceSignal2 = rail.distanceTo(signal2)
    return if (distanceSignal1 < distanceSignal2) signal1 else signal2
}

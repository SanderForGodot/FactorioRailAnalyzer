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
    //filter out entity's we don't care about
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
    entityList.railLinker(matrix)
    //endregion
    Graphviz().generateEntityRelations(entityList)
    //region Phase3: edge creation
    val signalList: ArrayList<Entity> = entityList.filter { entity ->
        entity.isSignal()
    } as ArrayList<Entity>

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
                &&edge.validRail!!
    }.forEach { edge ->
        val endingSignal = edge.last(1)
        edge.nextEdgeList = relation[endingSignal]!!
        notStartSignalList.addUnique(endingSignal)
    }
    //endregion
    if (listOfEdges.size == 0) {
        if (signalList.size == 0)
            throw Exception("No Edges Found, because there are no signals in the blueprint")
        throw Exception("No Edges Found, but there are some signals ")
    }
    //region Phase3: creating the blocks that are defined by the signals in factorio

    val blockList = connectEdgesToBlocks(listOfEdges)
    // creating the Graph out of the Blocks and edges
    val startSignals = signalList.toSet() - notStartSignalList.toSet()
    val graph: MutableMap<Int, MutableList<Int>> = mutableMapOf()
    blockList.filter { block ->
        block.isRelevant(startSignals)
    }.forEach { block ->
        graph[block.id] = block.findEnd().toMutableList()
    }

    //analysing the graph
    val graphTesting = Graph()
    graphTesting.setGraph(graph)
    graphTesting.tiernan()

    //debug output
    var i = 0
    listOfEdges.forEach {
        println(it)
        printEdge(it, i)
        i++

    }
    //endregion
}

fun connectEdgesToBlocks(listOfEdges: ArrayList<Edge>): ArrayList<Block> {
    val blockList: ArrayList<Block> = arrayListOf(Block(listOfEdges[0], 0))
    listOfEdges[0].belongsToBlock = blockList[0]
    var counter = 0
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
    return blockList
}

fun ArrayList<Entity>.railLinker(matrix: Array<Array<ArrayList<Entity>?>>) {
    //endregion
    this.filter { entity ->
        entity.isRail()
    }.forEach outer@{ rail -> // for Each (R)eal rail entity
        fact[rail.entityType]?.get(rail.direction)?.forEach inner@{ factEntity -> // for each entity T
            // calculate P = R + T
            val possiblePosition = rail.position + factEntity.position
            val x = floor(possiblePosition.x / 2).toInt()
            val y = floor(possiblePosition.y / 2).toInt()
            if (x < 0 || y < 0 || matrix.size <= x || matrix[0].size <= y) {
                return@inner
            }
            //look up P in matrix
            addEachMatchingEntity(matrix[x][y], factEntity, rail)
        }
    }
}

fun addEachMatchingEntity(entities: ArrayList<Entity>?, factEntity: Entity, rail: Entity) {
    entities?.filter { entity ->// for each existing Entity E
        // if T and E are equal (except position)
        (entity.isSignal() == factEntity.isSignal() ||
                entity.isRail() == factEntity.isRail())  //technically  this check is unnecessary
                && entity.direction == factEntity.direction
    }?.forEach { foundEntity ->
        // yes -> Add a reference from R to E to the direction depending on T
        if (foundEntity.isSignal()) {
            // for signal, we do tow way add and set an edge case var
            foundEntity.removeRelatedRail = setRemoveRelatedRail(foundEntity, factEntity, rail)
            foundEntity.getRailList(factEntity.entityNumber!!).addUnique(rail)
            rail.getSignalList(factEntity.entityNumber!!).addUnique(foundEntity)
        } else {
            rail.getRailList(factEntity.entityNumber!!).addUnique(foundEntity)
        }
    }
}

fun setRemoveRelatedRail(foundRail: Entity, factEntity: Entity, rail: Entity): Boolean {
    //set removeRelatedRail depending on foundRail (A) and possibleRail (input)
    val current = foundRail.removeRelatedRail
    val factVal = factEntity.removeRelatedRail!! //theoretical value determined by the fact.tk
    return when (current) {
        // if bool is not set take the input value
        null -> factVal
        // if the bool and the input have the same value all ok continue
        (factVal == current) -> current
        // if the rails disagree prioritise the curved rail state
        else -> (rail.entityType == EntityType.CurvedRail) == factVal

    }
}


//region Phase1 functions

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
    // the coordinate space is compress by 2 to reduce the amount of empty List, as the Rails are on a 2 by 2 coordinate space anyway
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
        val end = determineEndingSander(edge, direction)
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

    val goodSide = edge.last(1).getSignalList(direction).clone() as ArrayList<Entity>?
    val wrongSide = edge.last(1).getSignalList(-direction).clone() as ArrayList<Entity>?
    while (goodSide?.contains(edge.entityList.first()) == true) { //remove the starting node so that rail signals end themselves
        goodSide.remove(edge.entityList.first()) //todo re write this function
    }
    val hasWrong: Boolean =
        wrongSide?.isNotEmpty() ?: false // if there is a signal on the opposite side we assume problems
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
            assert(edge.entityList.size < 2)
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

package FRA

import Edge
import EntityType
import factorioBlueprint.Entity
import factorioBlueprint.Position

fun buildEdge(entity: Entity): List<Edge> {
    //var direction = if (entity.direction < 4) -1 else 1
    val result = mutableListOf<Edge>()
    if (entity.leftNextRail.size != 0)
        result.addAll(buildEdge(Edge(entity), -1))
    if (entity.rightNextRail.size != 0)
        result.addAll(buildEdge(Edge(entity), 1))
    return result
}

fun buildEdge(edge: Edge, direction: Int): List<Edge> {
    if (edge.last(1).hasSignal()) {
        val end = determineEnding(edge, direction)
        if (end != null)
            return arrayListOf(end)
        //otherwise continue and ignore (happens once at the start of every edg to ignore the starting signal)
    }
    //  val arr: ArrayList<Edge> = arrayListOf()
    return buildEdgeInner(edge, direction)

}

private fun buildEdgeInner(edge: Edge, direction: Int): List<Edge> {
    val nextRails = edge.last(1).getRailList(direction)
    return if (nextRails.size > 0) {

        nextRails.map { entity ->
            val modifier = isSpecialCase(edge.last(1), entity)
            buildEdge(Edge(edge, entity), direction * modifier)
        }.flatten()
    } else {
        val blankSignal = Entity(0, EntityType.VirtualSignal, Position(0.0, 0.0), 123, true)
        listOf(edge.finishUpEdge(blankSignal, true))
    }
}

fun buildEdgeReversed(entity: Entity): List<Edge> {
    val direction = if (entity.direction < 4) 1 else -1
    return entity.getRailList(direction * -1).map {
        // sikping the first self check
        buildEdgeInner(Edge(Edge(entity), it), direction)
    }.flatten().filter { it.validRail }.distinctBy { it.uniqueID() }
        .onEach {
            it.entityList.reverse()
            it.entityList.first().entityType = EntityType.Signal
            it.cleanAndCalc()
        }
}


//at specific transitions from rail a to b we need to flip the direction indicator
fun isSpecialCase(current: Entity, next: Entity): Int {
    val candidates = intArrayOf(0, 1, 4, 5)
    // sorts outs most cases to improve efficiency
    if (!candidates.contains(current.direction) || !candidates.contains(next.direction))
        return 1

    val edgeCases: Map<Pair<EntityType, Int>, Pair<EntityType, Int>> =
        mapOf(
            Pair(EntityType.CurvedRail, 0) to Pair(EntityType.Rail, 0),
            Pair(EntityType.CurvedRail, 5) to Pair(EntityType.CurvedRail, 0),
            Pair(EntityType.Rail, 0) to Pair(EntityType.CurvedRail, 4),
            Pair(EntityType.CurvedRail, 4) to Pair(EntityType.CurvedRail, 1),
        )
    Entity()
    return if ((edgeCases[current.signature()] == next.signature())
        || (edgeCases[next.signature()] == current.signature())
    ) -1 else 1
}


fun determineEnding(edge: Edge, direction: Int): Edge? {
    if (edge.last(1).toMannySignals()) {
        throw Exception("toMannySignals on strait rail")// todo: add to InvalidSignalList exception
    }
    val goodSide = edge.last(1).getSignalList(direction)            // in drive direction on the right site
    val wrongSide = edge.last(1).getSignalList(-direction)          // in drive direction on the left  site

    // signal name is viewed in the direction of travel
    val firstRight: Entity? = retrieveSignal(goodSide, false)
    val secondRight: Entity? = retrieveSignal(goodSide, true)
    val firstLeft: Entity? = retrieveSignal(wrongSide, true)
    val secondLeft: Entity? = retrieveSignal(wrongSide, false)
    val startSignal = edge.entityList.first()

    val endingSig: Entity? = if (goodSide.contains(startSignal)) {
        if (secondRight == startSignal) // !d   = x == startSignal
            null
        else if (goodSide.size == 2) // c  = goodSide.size == 2
            secondRight
        else
            secondLeft
    } else {
        //first before right | right before left
        val priorityOrder = arrayListOf(firstRight, firstLeft, secondRight, secondLeft)
        priorityOrder.first { it != null }!!
    }

    if (endingSig == null)
        return null
    val validRail = goodSide.contains(endingSig)
    return edge.finishUpEdge(endingSig, validRail)
}

fun retrieveSignal(signalList: ArrayList<Entity>, b: Boolean): Entity? {
    return if (signalList.size == 0)
        null
    else
        if (signalList[0].removeRelatedRail == b)
            signalList[0]
        else
            if (signalList.size == 1)
                null
            else // signalList.size == 2
                signalList[1]
}
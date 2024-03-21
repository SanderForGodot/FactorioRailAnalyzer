import factorioBlueprint.Entity
import factorioBlueprint.Position

fun buildEdge(edge: Edge, direction: Int, inverseSearch: Boolean): ArrayList<Edge> {
    var inverse = inverseSearch
    if (edge.last(1).hasSignal()) {
        if (inverse) {
            inverse = false

        } else {
            if (edge.last(1).toMannySignals()) {
                throw Exception("toMannySignals on strait rail")// todo: add to InvalidSignalList exception
            }
            val end = determineEnding(edge, direction)
            if (end != null)
                return arrayListOf(end)
            //otherwise continue and ignore (happens once at the start of every edg to ignore the starting signal)
        }
    }
    val arr: ArrayList<Edge> = arrayListOf()
    val nextRails = edge.last(1).getRailList(direction * (if (inverse) -1 else 1))
    if (nextRails.size > 0) {
        nextRails.forEach { entity ->
            val modifier = isSpecialCase(edge.last(1), entity)
            val result = buildEdge(Edge(edge, entity), direction * modifier, inverse)
            arr.addAll(result)
        }
    } else {
        val blankSignal = Entity(0, EntityType.VirtualSignal, Position(0.0, 0.0), 123, true)
        arr.add(edge.finishUpEdge(blankSignal, true))
    }

    return arr
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

fun determineEnding(edge: Edge, direction: Int): Edge? {
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
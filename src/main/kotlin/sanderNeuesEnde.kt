import factorioBlueprint.Entity

fun determineEndingSander(edge: Edge, direction: Int): Edge? {
    val goodSide = edge.last(1).getSignalList(direction)            // in drive direction on the right site
    val wrongSide = edge.last(1).getSignalList(-direction)          // in drive direction on the left  site

    // signal name viewed it in drive direction                        // equivalent if you view a Curved Rail of direction 2
    val firstRight: Entity? = retrieveSignal(goodSide, false)       // untenLinks  (this is how we first thought about the problem)
    val secondRight: Entity? = retrieveSignal(goodSide, true)       // untenRechts
    val firstLeft: Entity? = retrieveSignal(wrongSide, true)        // obenLinks
    val secondLeft: Entity? = retrieveSignal(wrongSide, false)      // obenRechts
    val startSignal = edge.EntityList.first()

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
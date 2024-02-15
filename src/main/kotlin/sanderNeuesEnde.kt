import factorioBlueprint.Entity
fun determineEndingSander(edge: Edge, direction: Int): Edge? {
    val goodSide = edge.last(1).getSignalList(direction)            // in drive direction on the right site
    val wrongSide = edge.last(1).getSignalList(-direction)          // in drive direction on the left  site

    // var namen relativ zu R2 einer gebogenen schiene (grafik)        // equivalent if you view it in drive direction
    val untenLinks: Entity? = retrieveSignal(goodSide, false)         // firstRight
    val untenRechts: Entity? = retrieveSignal(goodSide, true)         // secondRight
    val obenLinks: Entity? = retrieveSignal(wrongSide, true)          // firstLeft
    val obenRechts: Entity? = retrieveSignal(wrongSide, false)        // secondLeft
    val startSignal = edge.EntityList.first()

    val endingSig: Entity? = if (goodSide.contains(startSignal)) {
        if (untenRechts == startSignal) // !d   = x == startSignal
            null
        else if (goodSide.size == 2) // c    = goodSide.size == 2
            untenRechts
        else
            obenRechts
    } else {
        //left bevor right | unten vor oben
        val priorityOrder = arrayListOf(untenLinks, obenLinks, untenRechts, obenRechts)
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
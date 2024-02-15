import factorioBlueprint.Entity

class determineEndingClass {

    fun determineEndingNew(edge: Edge, direction: Int): Edge? {

        if (edge.last(1).entityType == EntityType.Rail) {
            return determineEndingForStraightRail(edge, direction)
        } else {
            return determineEndingForCurvedRail(edge, direction)
        }

        return null
    }

    fun determineEndingForStraightRail(edge: Edge, direction: Int): Edge? {
        val goodSide = edge.last(1).getSignalList(direction)?.clone() as ArrayList<Entity>?
        val wrongSide = edge.last(1).getSignalList(-direction)?.clone() as ArrayList<Entity>?
        val signalCount = (goodSide?.size ?: 0) + (wrongSide?.size ?: 0)

        when(signalCount){
            1->{return determineEndingForStraightRailOnesignal(edge, direction)}
            2->{return determineEndingForStraightRailTwosignals(edge, direction)}
            3->{throw Exception("Too many Signals on straight rail. It needs a lot of determination, to get into this state. You have really outdone yourself. ")}
            4->{throw Exception("Too many Signals on straight rail. It needs a lot of determination, to get into this state. You have really outdone yourself. ")}
        }
        return null
    }

    fun determineEndingForStraightRailOnesignal(edge: Edge, direction: Int):Edge?{
        val goodSide = edge.last(1).getSignalList(direction)?.clone() as ArrayList<Entity>?
        val wrongSide = edge.last(1).getSignalList(-direction)?.clone() as ArrayList<Entity>?

        if ((wrongSide?.size ?: 0) > 0){//ending rail with wrong side
            val falseSignal = wrongSide!!.first()
            // take the signal on the wrong side
            return edge.finishUpEdge(falseSignal, false)
        }
        if (goodSide != null) {
            if (goodSide.first() == edge.EntityList.first()){//starting rail
                return null //continue with edge creation
            }
            else{//ending rail with good side
                val endSignal = goodSide.first()
                //end edge with signal on the same side
                return edge.finishUpEdge(endSignal, true)
            }
        }
        throw Exception("determine Ending got wrong number of signals in straight rail")
    }

    fun determineEndingForStraightRailTwosignals(edge: Edge,direction: Int):Edge?{
        val goodSide = edge.last(1).getSignalList(direction)?.clone() as ArrayList<Entity>?
        val wrongSide = edge.last(1).getSignalList(-direction)?.clone() as ArrayList<Entity>?
        val startSignal = edge.EntityList.first()

        throw Exception("not yet implemented")
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

    fun isStartSignalAtEnd(edge: Edge): Boolean { // Should now be correct
        val rail = edge.last(1)
        val signal = edge.EntityList.first()

        var virtualsignal = fact[rail.entityType]?.get(rail.direction)?.filter { signal.direction == it.direction }
        return virtualsignal?.first()?.removeRelatedRail == true
        // removeRelatedRail correlates to the position of the signal, which in turn determines if it is a starting or ending position
        // somehow those are the same (blame sander) else build a new lookuptable(leo)
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

}
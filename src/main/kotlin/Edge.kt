import factorioBlueprint.Entity
import factorioBlueprint.Position

class Edge() {
    constructor(edge: Edge) : this() {
        clone(edge)
    }

    constructor(item: Entity) : this() {
        EntityList = arrayListOf(item)
    }

    constructor(edge: Edge, entity: Entity) : this() {
        clone(edge)
        if (done || EntityList.contains(entity)) {
            //    println(this)
            var aaaahhhh = 1123
        }
        if (!EntityList.addUnique(entity))
            throw Exception("an Edge is not expected to have the same rail twice")

    }

    lateinit var EntityList: ArrayList<Entity>
    var totalLength: Int = 0
    var collisionShape: ArrayList<Position> = arrayListOf();
    var belongsToBlock: Block? = null
    private var validRail: Boolean? = null
    var done = false
    var nextEdgeList: List<Edge> = arrayListOf()
    var tileLength: Double = 0.0 // how many tiles the edge is long, does not include correct block endings

    fun clone(edge: Edge) {
        EntityList = edge.EntityList
        totalLength = edge.totalLength
        collisionShape = edge.collisionShape
        belongsToBlock = edge.belongsToBlock
        validRail = edge.validRail
    }


    fun last(n: Int): Entity {
        // return EntityList.last();
        if (EntityList.size > n - 1) {
            return EntityList[EntityList.size - n];
        } else {
            return last(n - 1) // isnt this then just list[0]?
        }
    }

    fun finishUpEdge(signal: Entity, validRail: Boolean): Edge {
        EntityList.add(signal)
        this.validRail = validRail
        done = true
        cleanUpEndings()
        if (last(1).name == "blank-Signal")
            return this //TODO: set flags for being a ending edge or somthing
        generateCollision()
        return this
    }

    private fun cleanUpEndings() {
        val start = EntityList.first().removeRelatedRail
        val end = last(1).removeRelatedRail
        if (start == null || end == null) {
            println("removeRelatedRail flag has not been set")
            return
        }
        if (start) {
            EntityList.removeAt(1)
        }
        if (!end) // condition is inverted do to how Factorio works check docks //todo: add kapietel nummer
        {
            EntityList.removeAt(EntityList.size - 2)
        }
    }

    private fun generateCollision() { // for refence what wa dedtroyed
        if (EntityList.size < 3) return // if the list is only 2 long, there are only signals in the list and no rails

        //adding the starting point
        val start = EntityList.first()
        val firstRail = EntityList[1]
        if (!start.isSignal()) throw Exception("cannot calculate collisionShape, got rail: $firstRail, expected signal")
        if (!firstRail.isRail()) throw Exception("cannot calculate collisionShape, got signal: $start, expected rail")

        var listRef = collisionPoints[firstRail.entityType]?.get(firstRail.direction)!!.toMutableList()
        listRef[0] +=firstRail.position
        listRef[1] +=firstRail.position
        collisionShape.add(closer(start.position, listRef ))


        //adding 3 points for each curve (if the curves touch it will only add 2 points )
        val curves = collisionPoints[EntityType.CurvedRail]!!
        EntityList.filter {
            it.entityType == EntityType.CurvedRail
        }.forEach {
            var pointA = curves[it.direction]?.get(0)!! + it.position
            var pointB = curves[it.direction]?.get(1)!! + it.position
            val end = collisionShape[collisionShape.size - 1]
            if (pointB.x == end.x || pointB.y == end.y || pointB.x - end.x == pointB.y - end.y) {
                var tmp = pointA
                pointA = pointB
                pointB = pointA
            }
            if (collisionShape[collisionShape.size-1] != pointA )
                collisionShape.add(pointA)
            collisionShape.add(it.position)
            collisionShape.add(pointB)
        }


        val lastRail = last(2)
        listRef = collisionPoints[lastRail.entityType]?.get(lastRail.direction)!!.toMutableList()
        listRef[0] += lastRail.position
        listRef[1]+= lastRail.position
        val lastPoint = collisionShape[collisionShape.size-1]
        listRef.remove(closer(lastPoint, listRef))
        if (lastPoint != listRef[0] )
            collisionShape.add(listRef[0])

    }




    private fun LEOgenerateCollision() { //TODO: Check if the collisionshape is correct, or if the shape is shiftet into the wrong direction

        if (EntityList.size < 3) return // if the list is only 2 long, there are only signals in the list and no rails
        //adding the starting point
        //val start = EntityList.first().position
        if (EntityList.size == 3) { // if the list is only 3 long, there is only one rail in the edge and the other logic will not work
            val rail = EntityList[1]
            if (rail.isSignal()) throw Exception("cannot calculate collisionShape, got signal, expected rail")
            if (rail.entityType == EntityType.Rail) {
                var listRef = collisionPoints[rail.entityType]?.get(rail.direction)!!
                collisionShape.add(rail.position + listRef[0])
                collisionShape.add(rail.position + listRef[1])
            } else {
                var listRef = collisionPoints[rail.entityType]?.get(rail.direction)!!
                collisionShape.add(rail.position + listRef[0]) // TODO: Check (s.o.)
                collisionShape.add(rail.position) // TODO: Check (s.o.)
                collisionShape.add(rail.position + listRef[1]) // TODO: Check (s.o.)
            }
            return
        }
        // for the rest cases there are at least 2 rails to correctly calculate the collisionShape
        val firstSignal = EntityList[0]
        val firstRail = EntityList[1]
        if (firstRail.isSignal()) throw Exception("cannot calculate collisionShape, got signal: $firstRail, expected rail")
        if (firstSignal.isRail()) throw Exception("cannot calculate collisionShape, got rail: $firstSignal, expected signal")

        var listRef = collisionPoints[firstRail.entityType]?.get(firstRail.direction)!!

        collisionShape.add(firstRail.position + closer(firstSignal.position, listRef)) // TODO: Check (s.o.)


        //adding 3 points for each curve (if the curves touch it will only add 2 points )
        val curves = collisionPoints[EntityType.CurvedRail]!!
        EntityList.filter {
            it.name == "curved-rail"
        }.forEach {
            var pointA = curves[it.direction]?.get(0)!!
            var pointB = curves[it.direction]?.get(1)!!
            val end = collisionShape[collisionShape.size - 1]
            if (pointB.x == end.x || pointB.y == end.y || pointB.x - end.x == pointB.y - end.y) {
                var tmp = pointA
                pointA = pointB
                pointB = pointA
            }
            collisionShape.addUnique(it.position + pointA)  // TODO: Check (s.o.)
            collisionShape.add(it.position)
            collisionShape.add(it.position + pointB)  // TODO: Check (s.o.)
        }

        val endSignal = last(1)
        val lastRail = last(2)
        if (lastRail.isSignal()) throw Exception("cannot calculate collisionShape, got signal: $lastRail, expected rail")
        if (endSignal.isRail()) throw Exception("cannot calculate collisionShape, got rail: $endSignal, expected signal")
        listRef = collisionPoints[lastRail.entityType]?.get(lastRail.direction)!!
        if (endSignal.entityType != EntityType.VirtualSignal) {
            collisionShape.addUnique(lastRail.position + closer(endSignal.position, listRef))  // TODO: Check (s.o.)
        } else {//very different logic for a BlankSignal, since it's position is unknown
            if (lastRail.entityType != EntityType.CurvedRail) {//when the last rail is a curved rail, there is nothing to do,
                // since we already put all collisionpionts of the curvedrail into collisionshape
                val secondLastRail = last(3)//check with this rail, in which direction the edge was going
                if (secondLastRail.isSignal()) throw Exception("cannot calculate collisionShape, got signal: $secondLastRail, expected rail")
                var positionDifference = lastRail.position - secondLastRail.position
                if (lastRail.direction == 0 || lastRail.direction == 2) {
                    if (positionDifference.x == 0.0) {

                    } else if (positionDifference.y == 0.0) {

                    }
                }
            }
        }

    }

    //returns the closest of the 2 position to the signal position
    private fun closer(signal: Position, options: List<Position>): Position {
        return if (signal.distanceTo(options[0]) < signal.distanceTo(options[1])) {
            options[0]
        } else {
            options[1]

        }
    }

    fun doesCollide(other: Edge): Boolean {
        var A: Position
        var B: Position
        var C: Position
        var D: Position
        for (i in 0..collisionShape.size - 2) {
            A = collisionShape[i]
            B = collisionShape[i + 1]
            for (ii in 0..other.collisionShape.size - 2) {
                C = other.collisionShape[i]
                D = other.collisionShape[i + 1]
                if (intersect(A, B, C, D)) {
                    return true
                }
            }
        }
        return false

    }

    fun intersect(A: Position, B: Position, C: Position, D: Position): Boolean {
        return ccw(A, C, D) != ccw(B, C, D) && ccw(A, B, C) != ccw(A, B, D)
    }

    fun ccw(A: Position, B: Position, C: Position): Boolean {
        return (C.y - A.y) * (B.x - A.x) > (B.y - A.y) * (C.x - A.x)

    }


    override fun toString(): String {
        var str = "EdgeStart------------------\n"

        EntityList.forEach {
            val int: String = (it.entityNumber ?: 0).toString();
            str += int + "|" + it.name + "|" + it.position.toString() + "\n"

        }
        str += "tile length:$tileLength"
        return str
    }

    fun findEnd(): MutableList<Int> {
        if (EntityList.first().entityType == EntityType.Signal)
            return mutableListOf(belongsToBlock!!.id)
        else {
            var resultList = mutableListOf<Int>()
            nextEdgeList.forEach { edge ->
                resultList.addAll(edge.findEnd())
            }
            return resultList

        }
    }

    fun calcTileLength() {// calculates the tile length of the edge, todo decide if rounded is precise enough
        EntityList.forEach { entity ->
            if (entity.isRail()) {
                tileLength += when (entity.entityType) {
                    EntityType.Rail -> checkDiagonal(entity)
                    EntityType.CurvedRail -> 7.843 //rounded value exact value: 8.55-(sqrt(2)/2)
                    else -> throw Exception("Not a rail, should have been caught, how did you get here");
                }
            }
        }
    }

    private fun checkDiagonal(entity: Entity): Double {// returns the length of a straight or diagonal rail
        return when (entity.direction) {
            //normal straight rail
            0,2 -> 2.0
            //diagonal rails
            1,3,5,7 -> 1.414 //rounded value exact value: sqrt(2)
            else -> throw Exception("Not a straight rail, logically this shouldn't happen, how did you get here")
        }
    }
}
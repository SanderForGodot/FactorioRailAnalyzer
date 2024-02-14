import factorioBlueprint.Entity
import factorioBlueprint.Position
import kotlin.math.sqrt

class Edge() {
    constructor(edge: Edge) : this() {
        clone(edge)
    }

    constructor(item: Entity) : this() {
        EntityList = arrayListOf(item)
    }

    constructor(edge: Edge, entity: Entity) : this() {
        clone(edge)
        if (!EntityList.addUnique(entity))
            throw Exception("an Edge is not expected to have the same rail twice")

    }

    lateinit var EntityList: ArrayList<Entity>
    var collisionShape: ArrayList<Position> = arrayListOf();
    var belongsToBlock: Block? = null
    var validRail: Boolean? = null
    var nextEdgeList: List<Edge> = arrayListOf()
    var tileLength: Double = 0.0 // how many tiles the edge is long

    private fun clone(edge: Edge) {
        EntityList = edge.EntityList.clone() as ArrayList<Entity>
        collisionShape = edge.collisionShape.clone() as ArrayList<Position>
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
        cleanUpEndings()
        generateCollision()
        calcTileLength()
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
        listRef[0] += firstRail.position
        listRef[1] += firstRail.position
        collisionShape.add(closer(start.position, listRef))


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
            if (collisionShape[collisionShape.size - 1] != pointA)
                collisionShape.add(pointA)
            collisionShape.add(it.position)
            collisionShape.add(pointB)
        }


        val lastRail = last(2)
        listRef = collisionPoints[lastRail.entityType]?.get(lastRail.direction)!!.toMutableList()
        listRef[0] += lastRail.position
        listRef[1] += lastRail.position
        val lastPoint = collisionShape[collisionShape.size - 1]
        listRef.remove(closer(lastPoint, listRef))
        if (lastPoint != listRef[0])
            collisionShape.add(listRef[0])

    }


    //returns the closest of the 2 position to the signal position
    private fun closer(signal: Position, options: List<Position>): Position {
        return if (signal.distanceTo(options[0]) < signal.distanceTo(options[1])) {
            options[0]
        } else {
            options[1]

        }
    }

    fun calcTileLength() {// calculates the tile length of the edge
        tileLength = 0.0
        EntityList.filter {entity ->
            entity.isRail()
        }.forEach { entity ->
            tileLength += when (entity.entityType) {
                EntityType.Rail -> checkDiagonal(entity)
                EntityType.CurvedRail -> 7.843 //rounded value exact value: 8.55-(sqrt(2)/2)
                else -> throw Exception("Not a rail, should have been caught, how did you get here");
            }
        }
    }

    private fun checkDiagonal(entity: Entity): Double {// returns the length of a straight or diagonal rail
        return when (entity.direction) {
            //normal straight rail
            0, 2 -> 2.0
            //diagonal rails
            1, 3, 5, 7 -> sqrt(2.0)
            else -> throw Exception("Not a straight rail, logically this shouldn't happen, how did you get here")
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
    override fun toString(): String {
        var str = "EdgeStart------------------\n"

        EntityList.forEach {
            val int: String = (it.entityNumber ?: 0).toString();
            str += int + "|" + it.entityType.name + "|" + it.position.toString() + "\n"

        }
        str += "tile length:$tileLength"
        return str
    }

    fun debugPrint(): Pair<Boolean, Entity> {
        return Pair(validRail!!, last(1))
    }
}
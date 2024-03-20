import factorioBlueprint.Entity
import factorioBlueprint.Position
import kotlin.math.abs
import kotlin.math.sqrt

class Edge() : Grafabel {
    constructor(item: Entity) : this() {
        entityList = arrayListOf(item)
    }

    constructor(edge: Edge, entity: Entity) : this() {
        clone(edge)
        if (!entityList.addUnique(entity)) throw Exception("an Edge is not expected to have the same rail twice")

    }

    lateinit var entityList: ArrayList<Entity>
    var collisionShape: ArrayList<Position> = arrayListOf()
    var belongsToBlock: Block? = null
    var validRail: Boolean = false
    var nextEdgeList: List<Edge>? = null
    var tileLength: Double = 0.0 // how many tiles the edge is long

    private fun clone(edge: Edge) {
        entityList = edge.entityList.clone() as ArrayList<Entity>
        collisionShape = edge.collisionShape.clone() as ArrayList<Position>
        belongsToBlock = edge.belongsToBlock
        validRail = edge.validRail
    }

    fun nextEdgeListAL(): ArrayList<Edge> {
        if (nextEdgeList == null) return arrayListOf<Edge>()
        return nextEdgeList!! as ArrayList<Edge>
    }

    fun last(n: Int): Entity {
        // return EntityList.last();
        return if (entityList.size > n - 1) {
            entityList[entityList.size - n]
        } else {
            last(n - 1) // isn't this then just list[0]?
        }
    }

    fun finishUpEdge(signal: Entity, validRail: Boolean): Edge {

        entityList.add(signal)
        this.validRail = validRail
        return this
    }

    fun cleanAndCalc() {
        cleanUpEndings()
        generateCollision()
        calcTileLength()
    }

    private fun cleanUpEndings() {
        val start = entityList.first().removeRelatedRail
        val end = last(1).removeRelatedRail
        if (start == null || end == null) {
            println("removeRelatedRail flag has not been set")
            return
        }
        if (start) {
            entityList.removeAt(1)
        }
        if (!end) // condition is inverted do to how Factorio works check docks //todo: add a reference to the docs
        {
            entityList.removeAt(entityList.size - 2)
        }
    }

    private fun generateCollision() {
        if (entityList.size < 3) return // if the list is only 2 long, there are only signals in the list and no rails

        //adding the starting point
        val start = entityList.first()
        val firstRail = entityList[1]
        if (!start.isSignal()) throw Exception("cannot calculate collisionShape, got rail: $firstRail, expected signal")
        if (!firstRail.isRail()) throw Exception("cannot calculate collisionShape, got signal: $start, expected rail")

        var listRef = collisionPoints[firstRail.entityType]?.get(firstRail.direction)!!.toMutableList()
        listRef[0] += firstRail.position
        listRef[1] += firstRail.position
        collisionShape.add(closer(start.position, listRef))

        //adding 3 points for each curve (if the curves touch it will only add 2 points )
        val curves = collisionPoints[EntityType.CurvedRail]!!
        entityList.filter {
            it.entityType == EntityType.CurvedRail
        }.forEach {
            var pointA = curves[it.direction]?.get(0)!! + it.position
            var pointB = curves[it.direction]?.get(1)!! + it.position
            val end = collisionShape[collisionShape.size - 1]
            if (pointB.x == end.x || pointB.y == end.y || pointB.x - end.x == pointB.y - end.y) {
                val tmp = pointA
                pointA = pointB
                pointB = tmp
            }
            if (collisionShape[collisionShape.size - 1] != pointA) collisionShape.add(pointA)
            collisionShape.add(it.position)
            collisionShape.add(pointB)

        }

        val lastRail = last(2)
        if (lastRail.entityType == EntityType.Rail) {
            listRef = collisionPoints[lastRail.entityType]?.get(lastRail.direction)!!.toMutableList()
            listRef[0] += lastRail.position
            listRef[1] += lastRail.position
            val lastPoint = collisionShape[collisionShape.size - 1]
            listRef.remove(closer(lastPoint, listRef))
            collisionShape.addUnique(listRef[0])
        }
        collisionShape[0] = shortenEnds(collisionShape[0], collisionShape[1])
        collisionShape[collisionShape.size - 1] =
            shortenEnds(collisionShape[collisionShape.size - 1], collisionShape[collisionShape.size - 2])
        dbgPrintln("$collisionShape")

    }


    private fun shortenEnds(end: Position, second: Position): Position {
        val deltaX = abs(end.x - second.x)
        val deltaY = abs(end.y - second.y)
        val deltaDelta = deltaX - deltaY

        return when {
            deltaDelta == 0.0 -> {
                // generate diagonal points
                val A1 = end + Position(0.1, 0.1)
                val A2 = end + Position(-0.1, 0.1)
                val A3 = end + Position(0.1, -0.1)
                val A4 = end + Position(-0.1, -0.1)
                val A12 = closer(second, arrayListOf(A1, A2))
                val A34 = closer(second, arrayListOf(A3, A4))
                val R = closer(second, arrayListOf(A12, A34))
                R
            }

            deltaDelta < 0.0 -> {
                // Y is bigger
                // generate Y points
                val A1 = end + Position(0.0, 0.1)
                val A2 = end + Position(0.0, -0.1)
                val R = closer(second, arrayListOf(A1, A2))
                R
            }

            deltaDelta > 0.0 -> {
                // X is bigger
                // generate X points
                val A1 = end + Position(0.1, 0.0)
                val A2 = end + Position(-0.1, 0.0)
                val R = closer(second, arrayListOf(A1, A2))
                R
            }

            else -> throw Exception("actually impossible")
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

    private fun calcTileLength() {// calculates the tile length of the edge
        tileLength = 0.0
        entityList.filter { entity ->
            entity.isRail()
        }.forEach { entity ->
            tileLength += when (entity.entityType) {
                EntityType.Rail -> if (entity.direction % 2 == 0) 2.0 else sqrt(2.0)
                EntityType.CurvedRail -> 7.843 //rounded value exact value: 8.55-(sqrt(2)/2)
                else -> throw Exception("Not a rail, should have been caught, how did you get here")
            }
        }
    }

    fun doesCollide(other: Edge): Boolean {
        var pointA: Position
        var pointB: Position
        var pointC: Position
        var pointD: Position
        for (i in 0..collisionShape.size - 2) {
            pointA = collisionShape[i]
            pointB = collisionShape[i + 1]
            for (ii in 0..other.collisionShape.size - 2) {
                pointC = other.collisionShape[ii]
                pointD = other.collisionShape[ii + 1]
                if (intersect(pointA, pointB, pointC, pointD)) {
                    return true
                }
                //if(pointA ==pointC || pointA == pointD || pointB == pointC || pointB==pointD) {
                //    return true
                //}
            }
        }
        return false

    }

    override fun uniqueID(): Int {
        var start = entityList.first().entityNumber!!
        var end = last(1).entityNumber!!
        return start * 7 + end * 13 //todo: give edges a bedder unique id
    }

    override fun pos(): Position {
        val posList = entityList
            .filter { it.entityType == EntityType.VirtualSignal }
            .map { it.position }
        return posList
            .fold(Position(0.0, 0.0), Position::plus) / posList.size
    }

    override fun hasRailSignal(): Boolean {
        return entityList.first().entityType == EntityType.Signal
    }


    override fun toString(): String {
        return aToB()
    }

    fun oldToString(): String {
        var str = "EdgeStart--\n"

        entityList.forEach {
            val int: String = (it.entityNumber ?: 0).toString()
            str += int + "|" + it.entityType.name + "|" + it.position.toString() + "\n"

        }
        str += "tile length:$tileLength"
        return str
    }

    fun aToB(): String {
        return entityList.first().entityNumber.toString() + "->" + entityList.last().entityNumber.toString()
    }

    fun debugPrint(): Pair<Boolean, Entity> {
        return Pair(validRail!!, last(1))
    }

    var wasIchBeobachte = ArrayList<Edge>()
    fun setzteBeobachtendeEdges() {
        if (nextEdgeList == null) dbgPrintln("nextEdgeList was null")
        val toCheck: MutableList<Edge> = nextEdgeList?.toMutableList() ?: return

        val tCI: MutableListIterator<Edge> = toCheck.listIterator()
        var nextEdge: Edge = Edge()
        while (tCI.hasNext()) {
            nextEdge = tCI.next()
            if (nextEdge.belongsToBlock!!.istjemandrarwIchBinGefärlich()) wasIchBeobachte.addUnique(nextEdge)
            else {
                when (nextEdge.entityList.first().entityType) {
                    EntityType.Signal -> wasIchBeobachte.addUnique(nextEdge)

                    EntityType.ChainSignal -> {
                        if (nextEdge.nextEdgeList == null) {
                            // edge is a final edge
                            wasIchBeobachte.addUnique(nextEdge)
                            continue

                        }
                        nextEdge.nextEdgeList!!.filter { !toCheck.contains(it) }.forEach {
                            tCI.add(it)
                            tCI.previous()
                        }

                    }

                    else -> throw Exception("unexpected entityType: " + nextEdge.entityList.first().entityType + "\n Full Obj:" + nextEdge)
                }
            }

        }
    }

    fun byListIterator(list: MutableList<String>) {
        val it = list.listIterator()
        for (e in it) {
            if (e.length > 1) {
                it.add("<- a long one")
            }
        }
    }

    var rarwIchBinGefärlich = false
    fun setDanger() {
        rarwIchBinGefärlich = true
        if (last(1).entityType == EntityType.Signal) {
            nextEdgeList?.forEach {
                it.setDanger()
            }
        }
    }

    //uncertainty if inline is good /bad / required
    //https://kotlinlang.org/docs/inline-functions.html


}


import factorioBlueprint.Entity
import factorioBlueprint.Position

class Edge
    () {
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
        if(!EntityList.addUnique(entity))
            throw Exception("an Edge is not expected to have the same rail twice")

    }

    lateinit var EntityList: ArrayList<Entity>
    var totalLength: Int = 0
    var colisionShape: ArrayList<Position> = arrayListOf();
    var belongsToBlock: Block = Block()
    private var validRail: Boolean? = null
    var done = false

    fun clone(edge: Edge) {
        EntityList = edge.EntityList
        totalLength = edge.totalLength
        colisionShape = edge.colisionShape
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
            EntityList.removeAt(2)
        }
        if (!end) // condition is inverted do to how Factorio works check docks //todo: add kapietel nummer
        {
            EntityList.removeAt(EntityList.size - 2)
        }
    }

    private fun generateCollision() {
        //adding the starting point
        val start = EntityList.first().position
        val firstRail = EntityList[1]
        var listRef = collisionPoints[firstRail.name]?.get(firstRail.direction)!!
        colisionShape.add(closer(start, listRef))


        //adding 3 points for each curve (if the curves touch it will only add 2 points )
        val curves = collisionPoints["curved-rail"]!!
        EntityList.filter {
            it.name == "curved-rail"
        }.forEach {
            var pointA = curves[it.direction]?.get(0)!!
            var pointB = curves[it.direction]?.get(1)!!
            val end = colisionShape[colisionShape.size - 1]
            if (pointB.x == end.x || pointB.y == end.y || pointB.x - end.x == pointB.y - end.y) {
                var tmp = pointA
                pointA = pointB
                pointB = pointA
            }
            colisionShape.addUnique(pointA)
            colisionShape.add(it.position)
            colisionShape.add(pointB)
        }

        val endSignal = last(1).position
        val lastRail = last(2)
        listRef = collisionPoints[lastRail.name]?.get(lastRail.direction)!!
        colisionShape.addUnique(closer(endSignal, listRef))

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
        for (i in 0..colisionShape.size - 2) {
            A = colisionShape[i]
            B = colisionShape[i + 1]
            for (ii in 0..other.colisionShape.size - 2) {
                C = other.colisionShape[i]
                D = other.colisionShape[i + 1]
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

        return str
    }

}
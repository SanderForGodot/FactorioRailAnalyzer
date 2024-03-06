import factorioBlueprint.Position

class Block(edge: Edge, var id: Int) {
    var edgeList = arrayListOf(edge)
    var dependingOn: ArrayList<Block>? = null

    fun doesCollide(toTest: Edge): Boolean {
        return edgeList.any {
            it.doesCollide(toTest)
        }
    }

    fun merge(otherBlock: Block) {
        otherBlock.edgeList.forEach { edge ->
            edgeList.add(edge)
            edge.belongsToBlock = this
        }
    }

    fun hasRailSignal(): Boolean {
        return edgeList.any { edge ->
            (edge.entityList.first().entityType == EntityType.Signal)
        }
    }


    fun calculateCenter(): Position {
        var result = Position(0.0, 0.0)
        var count = 0
        edgeList.forEach { edge ->
            edge.entityList.filter { entity ->
                entity.isRail()
            }.forEach { rail ->
                result += rail.position
                count++
            }
        }
        return result / count
    }

        fun neighbourBlocks(): ArrayList<Block> {
        val neighbours = arrayListOf<Block>()
        edgeList.forEach { edge ->
            edge.nextEdgeList.forEach { nextEdge ->
                neighbours.add(nextEdge.belongsToBlock!!)
            }
        }
        return neighbours
    }

    fun edgeListSting(): String {
        var R = "["
        edgeList.forEach {
            R += it.aToB() + ", "

        }
        return R + "]"
    }

    override fun toString(): String {
        return "(id=$id,size=${edgeList.size}, Relevant=${hasRailSignal()})"
    }
}



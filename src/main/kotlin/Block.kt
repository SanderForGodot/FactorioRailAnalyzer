import factorioBlueprint.Position

class Block(edge: Edge, var id: Int) : Grafabel {
    var edgeList = arrayListOf(edge)
    var dependingOn: ArrayList<Block> = arrayListOf()

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

    override fun hasRailSignal(): Boolean {
        return edgeList.any { edge ->
            (edge.hasRailSignal())
        }
    }

    fun calculateCenter(): Position {
        var result = Position(0.0, 0.0)
        var count = 0
        edgeList.forEach { edge ->
            edge.entityList.filter { entity ->
                entity.entityType != EntityType.VirtualSignal
            }.forEach { rail ->
                result += rail.position
                count++
            }
        }
        return result / count
    }

    fun directNeighbours(): ArrayList<Block> {
        return edgeList.mapNotNull { edge ->
            edge.nextEdgeList?.mapNotNull {
                it.belongsToBlock
            }
        }.flatten().distinct().toMutableList() as ArrayList<Block>
    }

    @Deprecated("todo rework")
    fun edgeListSting(): String {
        var result = "["
        edgeList.forEach {
            result += it.aToB() + ", "

        }
        return "$result]"
    }

    override fun uniqueID(): Int {
        return id
    }

    override fun pos(): Position {
        return calculateCenter()
    }


    override fun toString(): String {
        return " id:$id "
    }

    fun anyEntryFlag(): Boolean {
        return edgeList.any { it.entryFlag }
    }
}



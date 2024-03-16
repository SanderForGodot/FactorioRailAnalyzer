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

    fun hasRailSignal(): Boolean {
        return edgeList.any { edge ->
            (edge.entityList.first().entityType == EntityType.Signal)
        }
    }

    fun hasMixedSignals(): Boolean {
        var signalExists = edgeList.any { edge ->
            (edge.entityList.first().entityType == EntityType.Signal)
        }
        var chainSignalExists = edgeList.any { edge ->
            (edge.entityList.first().entityType == EntityType.ChainSignal)
        }
        return signalExists and chainSignalExists;
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

    /*
        fun calculateCenter(): Position {
            val posList = edgeList.map {
                it.pos()
            }
            return posList.fold(Position(0.0,0.0),Position::plus) /posList.size
        }*/
    fun neighbourBlocks(): ArrayList<Block> {
        val neighbours = arrayListOf<Block>()
        edgeList.forEach { edge ->
            edge.wasIchBeobachte.forEach { nextEdge ->
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

    override fun uniqueID(): Int {
        return id
    }

    override fun pos(): Position {
        return calculateCenter()
    }


    override fun toString(): String {
        return "(id=$id,size=${edgeList.size}, Relevant=${hasRailSignal()})"
    }

    fun istjemandrarwIchBinGefärlich(): Boolean {
        return edgeList.any { it.rarwIchBinGefärlich }
    }
}



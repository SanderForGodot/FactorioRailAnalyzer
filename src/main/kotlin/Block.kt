import factorioBlueprint.Entity
import factorioBlueprint.Position

class Block(edge: Edge, var id:Int) {
    var edgeList = arrayListOf(edge)

    fun doesCollide(toTest:Edge):Boolean
    {
       return edgeList.any{
            it.doesCollide(toTest)
        }
    }

    fun merge(otherBlock: Block){
        otherBlock.edgeList.forEach{edge ->
            edgeList.add(edge)
            edge.belongsToBlock = this
        }
    }

    fun isRelevant(startSignalList: Set<Entity>): Boolean {
        return edgeList.any{edge->
            (edge.entityList.first().entityType == EntityType.Signal)
                    ||
           ( startSignalList.any()
            {signal->
                edge.entityList.first() == signal
            })
        }
    }

    fun findEnd():ArrayList<Int> {
        val resultList = ArrayList<Int>()
        edgeList.forEach { edge ->
            //resultList.addAll( edge.findEnd())
            val y = edge.findEnd()
            y.forEach{yp->
                resultList.addUnique(yp)
            }
        }
        return resultList
    }
    fun calculateCenter(): Position
    {
        var result = Position(0.0,0.0)
        var count =0
        edgeList.forEach{edge ->
            edge.entityList.filter {entity ->
                entity.isRail()
            }.forEach {rail->
                result += rail.position
                count++
            }
        }
        return result / count
    }
}



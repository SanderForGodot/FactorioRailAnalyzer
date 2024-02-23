import factorioBlueprint.Entity
import factorioBlueprint.Position

class Block(edge: Edge, var id:Int) {
    var edgeList = arrayListOf(edge)
    lateinit var dependingOn: ArrayList<Block>
    var isRelevant: Boolean = false


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

    fun markRelevant(startSignalList: Set<Entity>) {
        isRelevant= edgeList.any{edge->
            (edge.entityList.first().entityType == EntityType.Signal)
                    ||
           ( startSignalList.any()
            {signal->
                edge.entityList.first() == signal
            })
        }
    }

    fun findEnd():ArrayList<Block> {
        val resultList = ArrayList<Block>()
        edgeList.forEach { edge ->
            //resultList.addAll( edge.findEnd())
            val y = edge.findEnd(true)
            y.forEach{yp->
                resultList.addUnique(yp)
            }
        }
        this.dependingOn= resultList
        return resultList

    }

    fun calculateCenter(): Position
    {
        var result = Position ( 0.0, 0.0)
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



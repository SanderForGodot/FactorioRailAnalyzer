import factorioBlueprint.Entity

class Block(edge: Edge, var id:Int) {
    var edgeList = arrayListOf<Edge>(edge)

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

    fun isRelevant(startSignales: Set<Entity>): Boolean {
        return edgeList.any{edge->
            (edge.entityList.first().entityType == EntityType.Signal) //todo: constant string auslagern
                    ||
           ( startSignales.any()
            {signal->
                edge.entityList.first() == signal
            })
        }
    }

    fun findEnd():ArrayList<Int> {
        var resultList = ArrayList<Int>()
        edgeList.forEach { edge ->
            //resultList.addAll( edge.findEnd())
            var y = edge.findEnd()
            y.forEach{yp->
                resultList.addUnique(yp)
            }
        }
        return resultList
    }


}



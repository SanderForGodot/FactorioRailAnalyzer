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
            (edge.EntityList.first().name == "rail-signal") //todo: constant string auslagern
                    ||
           ( startSignales.any()
            {signal->
                edge.EntityList.first() == signal
            })
        }
    }

    fun findEnd():MutableList<Int> {
        var resultList = mutableListOf<Int>()
        edgeList.forEach { edge ->
            resultList.addAll( edge.findEnd())
        }
        return resultList
    }


}



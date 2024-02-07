import java.util.*

class Block(edge: Edge) {
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
}



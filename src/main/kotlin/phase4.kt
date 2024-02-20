
fun connectEdgesToBlocks(listOfEdges: ArrayList<Edge>): ArrayList<Block> {
    val blockList: ArrayList<Block> = arrayListOf(Block(listOfEdges[0], 0))
    listOfEdges[0].belongsToBlock = blockList[0]
    var counter = 0
    listOfEdges.filter { edge ->
        listOfEdges.first() != edge
    }.forEach { edge ->
        blockList.forEach { block ->
            if (block.doesCollide(edge)) {
                if (edge.belongsToBlock == null) {
                    block.edgeList.add(edge)
                    edge.belongsToBlock = block
                } else {
                    edge.belongsToBlock!!.merge(block)
                    blockList.remove(block)
                }
            }
        }
        if (edge.belongsToBlock == null) {
            counter++
            val newBlock = Block(edge, counter)
            edge.belongsToBlock = newBlock
            blockList.add(newBlock)
        }
    }
    return blockList
}


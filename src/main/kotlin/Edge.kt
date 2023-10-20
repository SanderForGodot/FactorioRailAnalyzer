import java.util.*

class Edge
    ( )
{
    constructor(edge: Edge) : this() {
        ItemList = edge.ItemList
        totalLength= edge.totalLength
        ColisionShape = edge.ColisionShape
        belongsToBlock = edge.belongsToBlock
    }
    constructor(item: Item):this(){
        ItemList.add(item)
    }

    lateinit var ItemList : ArrayList<Item>
    var totalLength : Int = 0
    lateinit var ColisionShape : ArrayList<Pair< Int, Int>>;
    lateinit var belongsToBlock : Block
}
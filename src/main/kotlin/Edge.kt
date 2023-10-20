import java.util.*

class Edge
    ( var ItemList : ArrayList<Item>)
{
    constructor(edge: Edge){
        ItemList = edge.ItemList
        totalLength= edge.totalLength
        ColisionShape = edge.ColisionShape
        belongsToBlock = edge.belongsToBlock
    }

    var totalLength : Int
    var ColisionShape : ArrayList<(Int,Int)>
    var belongsToBlock : Block
}
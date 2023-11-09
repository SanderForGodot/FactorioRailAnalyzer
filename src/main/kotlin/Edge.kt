import factorioBlueprint.Entity
import java.util.*

class Edge
    ( )
{
    constructor(edge: Edge) : this() {
        EntityList = edge.EntityList
        totalLength= edge.totalLength
        ColisionShape = edge.ColisionShape
        belongsToBlock = edge.belongsToBlock
    }
    constructor(item: Entity):this(){
        EntityList = arrayListOf(item)
    }

    lateinit var EntityList : ArrayList<Entity>
    var totalLength : Int = 0
    lateinit var ColisionShape : ArrayList<Pair< Int, Int>>;
    lateinit var belongsToBlock : Block
}
import factorioBlueprint.Entity
import java.util.*

class Edge
    () {
    constructor(edge: Edge) : this() {
        clone(edge)
    }

    constructor(item: Entity) : this() {
        EntityList = arrayListOf(item)
    }

    constructor(edge: Edge, entity: Entity) : this() {
        clone(edge)
        EntityList.add(entity)

    }

    lateinit var EntityList: ArrayList<Entity>
    var totalLength: Int = 0
    lateinit var ColisionShape: ArrayList<Pair<Int, Int>>;
    lateinit var belongsToBlock: Block

    fun clone(edge: Edge) {
        EntityList = edge.EntityList
        totalLength = edge.totalLength
        ColisionShape = edge.ColisionShape
        belongsToBlock = edge.belongsToBlock
    }

    fun last(): Entity {
        return EntityList.last();
    }

    fun secondLast(): Entity {
        if (EntityList.size > 1) {
            return EntityList[EntityList.size - 2];
        } else {
            return last()
        }
    }
}
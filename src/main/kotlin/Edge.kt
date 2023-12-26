import factorioBlueprint.Entity

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
        if (done || EntityList.contains(entity)) {
        //    println(this)
            var aaaahhhh = 1123
        }
        EntityList.addUnique(entity)

    }

    lateinit var EntityList: ArrayList<Entity>
    var totalLength: Int = 0
    var ColisionShape: ArrayList<Pair<Int, Int>> = arrayListOf();
    var belongsToBlock: Block = Block()
    private var validRail: Boolean? = null
    var done = false

    fun clone(edge: Edge) {
        EntityList = edge.EntityList
        totalLength = edge.totalLength
        ColisionShape = edge.ColisionShape
        belongsToBlock = edge.belongsToBlock
        validRail = edge.validRail
    }

    fun last(n: Int): Entity {
        // return EntityList.last();
        if (EntityList.size > n - 1) {
            return EntityList[EntityList.size - n ];
        } else {
            return last(n - 1)
        }
    }

    fun finishUpEdge(signal: Entity, validRail: Boolean): Edge {
        EntityList.add(signal)
        this.validRail = validRail
        done = true
        return this
    }

    override fun toString(): String {
        var str = "EdgeStart------------------\n"
        EntityList.forEach {
            str += it.name + "|" + it.position.toString() + "\n"
        }
        return str
    }

}
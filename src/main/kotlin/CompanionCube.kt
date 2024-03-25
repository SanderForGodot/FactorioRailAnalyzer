import com.google.gson.Gson
import factorioBlueprint.Entity
import factorioBlueprint.Position
import factorioBlueprint.ResultBP
import graph.Graph
import graph.tiernan


class CompanionCube(blueprint: String) {


    val entityList: ArrayList<Entity>

    init {
        if (blueprint.contains("blueprint_book")) {
            throw Exception("Sorry, a blueprint book cannot be parsed by this program, please input only Blueprints ")
        }
        val resultBP: ResultBP = Gson().fromJson(blueprint, ResultBP::class.java)
        this.entityList = resultBP.blueprint.entities
    }

    // every var nead a good justification
    private lateinit var max: Position  // required for the visualization later on
    private lateinit var signalList: List<Entity> // todo justify
    private lateinit var edgeList: ArrayList<Edge>
    lateinit var blockList: ArrayList<Block>
    fun removeNull(): CompanionCube { //todo if this dosent change move to init
        entityList.removeAll {
            it.entityType == null //it can be null the ide is lying (GSON brakes kotlin null safety)
        }
        return this
    }

    fun normalizePositions(): CompanionCube {
        val (min, max) = entityList.determineMinMax()
        // normalize coordinate space to start at 1, 1 (this makes the top left rail-corner be at 0.0)
        max -= min
        entityList.forEach { entity ->
            entity.position = entity.position - min
        }
        this.max = max
        return this
    }

    fun linkRails(): CompanionCube {
        val matrix = entityList.filedMatrix(max)
        entityList.railLinker(matrix)
        return this
    }

    fun genSignalList(): CompanionCube {//todo if this dosent change move to init
        signalList = entityList.filter { entity -> entity.isSignal() }
        if (signalList.isEmpty())
            throw Exception("No Signals in blueprint")
        //todo: create custom construction error
        return this;
    }

    fun genEdgeList(): CompanionCube {
        val relation = signalList.genRelation()

        edgeList = relation.map { it.value }.flatten().toMutableList() as ArrayList<Edge>
        val backwardsEdges = signalList.map {
            buildEdgeReversed(it)
        }.flatten()
        edgeList.addAll(backwardsEdges)

        edgeList.filter { edge ->
            edge.last(1).entityType != EntityType.VirtualSignal && edge.validRail
        }.forEach { edge ->
            edge.nextEdgeList = relation[edge.last(1).entityNumber]
        }
        backwardsEdges.forEach { it.setEntry() }
        return this
    }

    fun connectEdgesToBlocks(): CompanionCube {
        blockList = connectEdgesToBlocks(this.edgeList)
        return this
    }

    fun createVisualization(g: Graph<Block>) {
        svgFromPoints(max, edgeList, signalList, Position(-10.0, -10.0))
        blockList.visualize("neighborBlocks", g) { it.directNeighbours() }
        val d = blockList.tiernan {
            it.dependingOn
        }
        blockList.visualize("blockDependency", d) { it.dependingOn }
        edgeList.visualizeWithRef("monitoredEdgeList", { it.monitoredEdgeList }) { it.belongsToBlock }
        Graphviz().generateEntityRelations(entityList)
    }


}


private fun List<Entity>.genRelation(): Map<Int, MutableList<Edge>> {
    return this.map {
        (it.entityNumber!!) to
                buildEdge(it)
    }.groupBy({
        it.first
    }, {
        it.second
    }).mapValues { it ->
        it.value.flatten().distinctBy { it.uniqueID() }
            .onEach { it.cleanAndCalc() }
            .toMutableList()
    }
}

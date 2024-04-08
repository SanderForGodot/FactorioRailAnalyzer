import graph.tiernan

fun NEWfactorioRailAnalyzer(blueprint: String): Boolean {

    var cc = CompanionCube(blueprint)
        .normalizePositions()
        .linkRails()
        .genEdgeList()
        .connectEdgesToBlocks()
    var g = cc.blockList
        .tiernan {
            it.directNeighbours()
        }.reduceBasic()
        .analysis()

    cc.createVisualization(g)







    return g.hasDeadlocks()
}
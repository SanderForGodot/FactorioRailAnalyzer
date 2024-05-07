package FRA

fun factorioRailAnalyzer(blueprint: String): Boolean {

    val cc = CompanionCube(blueprint)
        .normalizePositions()
        .linkRails()
        .genEdgeList()
        .connectEdgesToBlocks()
    val g = cc.blockList
        .tiernan {
            it.directNeighbours()
        }.reduceBasic()
        .analysis()

    cc.createVisualization(g)

    return g.hasDeadlocks()
}
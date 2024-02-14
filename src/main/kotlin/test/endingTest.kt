import com.google.gson.Gson
import factorioBlueprint.Entity
import factorioBlueprint.Position
import factorioBlueprint.ResultBP

fun main() {
    //region Setup
    val jsonString: String = decodeBpSting("oben.txt")

    val resultBP = Gson().fromJson(jsonString, ResultBP::class.java)
    val listOben = resultBP.blueprint.entities

    listOben.retainAll {
        it.entityType != EntityType.Error
    }
    // determinant min and max of BP
    val (min, max) = listOben.determineMinMax()
    // normalize coordinate space to start at 1, 1 (this makes the top left rail-corner be at 0.0)
    max -= min
    listOben.forEach { entity ->
        entity.position = entity.position - min
    }

    val matrix = listOben.filedMatrix(max)
    listOben.railLinker(matrix)
    //endregion

    var solutionOben: ArrayList<Any?> =
        arrayListOf(
            null,
            Pair(
                true,
                fact[EntityType.CurvedRail]!![2]!!.first { it.entityType == EntityType.AnySignal && it.direction == 5 }),
            Pair(
                true,
                fact[EntityType.CurvedRail]!![2]!!.first { it.entityType == EntityType.AnySignal && it.direction == 5 }),
            Pair(
                true,
                fact[EntityType.CurvedRail]!![2]!!.first { it.entityType == EntityType.AnySignal && it.direction == 5 }),
            null,
            Pair(
                false,
                fact[EntityType.CurvedRail]!![2]!!.first { it.entityType == EntityType.AnySignal && it.direction == 1 }),
            Pair(
                false,
                fact[EntityType.CurvedRail]!![2]!!.first { it.entityType == EntityType.AnySignal && it.direction == 1 }),
            Pair(
                true,
                fact[EntityType.CurvedRail]!![2]!!.first { it.entityType == EntityType.AnySignal && it.direction == 5 }),
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
        )


    var edgeListOben = arrayListOf<Edge>()
    listOben.filter { entity ->
        entity.entityType == EntityType.ChainSignal
    }.sortedBy {
        it.position.y
    }.sortedBy {
        it.position.x
    }.forEach { ChailSig ->
        println(ChailSig.position)
        var edge = Edge(ChailSig)
        edge = Edge(edge, ChailSig.rightNextRail.first())
        edgeListOben.add(edge)
    }
    var ergebnise = arrayListOf<Any?>()
    edgeListOben.forEach { edge ->
        ergebnise.add(determineEnding(edge, 1))
    }
    var i = -1
    while (i < solutionOben.size - 1) {

        i++
        if (solutionOben[i] == null) {
            if (ergebnise[i] == null)
                println("Erfolg case:" + i)
            else {
                var t = (ergebnise[i] as Edge).debugPrint()
                println("Fail case:" + i + " should be " + solutionOben[i] + " but is" +t.first + " " + t.second.testDebug())
            }
            continue
        }
        var edge = ergebnise[i] as Edge
        var lösug = solutionOben[i] as Pair<Boolean, Entity>
        lösug.second.position += edge.EntityList[1].position
        if (edge.validRail == lösug.first && edge.last(1) == lösug.second)
            println("Erfolg case:" + i)
        else
            println("Fail case:" + i + " should be " + solutionOben[i] + " but is" + Pair(edge.validRail, edge.last(1).testDebug()))
    }

}
import com.google.gson.Gson
import factorioBlueprint.Entity
import factorioBlueprint.ResultBP

fun main() {
    val solutionOben: ArrayList<Any?> =
        arrayListOf(
            null,
            Pair(true, getFact(5)),
            Pair(true, getFact(5)),
            Pair(true, getFact(5)),
            null,
            Pair(false, getFact(1)),
            Pair(false, getFact(1)),
            Pair(true, getFact(5)),
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
        )
    val solutionUnten: ArrayList<Any?> =
        arrayListOf(
            Pair(true, getFact(6)),    //0
            Pair(true, getFact(6)),
            Pair(false, getFact(2)),
            Pair(true, getFact(6)),    //3
            Pair(true, getFact(5)),
            Pair(true, getFact(6)),     //5
            Pair(true, getFact(6)),     //6
            Pair(false, getFact(2)),    //7
            Pair(true, getFact(6)),     //8
            Pair(true, getFact(6)),
            Pair(false, getFact(1)),    //10
            Pair(true, getFact(5)),     //11
            Pair(true, getFact(6)),     //12
            Pair(false, getFact(2)),
            Pair(false, getFact(2)),
        )
    test("oben.txt", solutionOben)
   test("unten.txt", solutionUnten)

}
fun test(filename:String, solution:ArrayList<Any?>){
    //region Setup
    val jsonString: String = decodeBpSting(filename)

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





    val edgeListOben = arrayListOf<Edge>()
    listOben.filter { entity ->
        entity.entityType == EntityType.ChainSignal
    }.sortedBy {
        it.position.y
    }.sortedBy {
        it.position.x
    }.forEach { ChailSig ->
        var edge = Edge(ChailSig)
        edge = Edge(edge, ChailSig.rightNextRail.first())
        if (filename =="unten.txt" )
            edge = Edge(edge,edge.last(1).rightNextRail.first())

        edgeListOben.add(edge)
    }
    var padpw = 0
    val ergebnise = arrayListOf<Any?>()
    edgeListOben.forEach { edge ->
        println(padpw)
        padpw ++
        ergebnise.add(determineEnding(edge, 1))
    }
    println(filename)
    var i = -1
    while (i < (ergebnise.size - 1)) {

        i++
        if (solution[i] == null) {
            if (ergebnise[i] == null)
                println("Erfolg case:" + i)
            else {
                val t = (ergebnise[i] as Edge).debugPrint()
                println("Fail case:" + i + " should be " + solution[i] + " but is" + t)
            }
            continue
        }
        if (ergebnise[i] == null) {
            println("Fail case:" + i + " should be " + solution[i] + " but is null")
            continue
        }
        val edge = ergebnise[i] as Edge
        val correct = solution[i] as Pair<Boolean, Entity>
        correct.second.position += edge.last(2).position
        if (edge.validRail == correct.first && edge.last(1).direction == correct.second.direction)
            println("Erfolg case:" + i)
        else
            println("Fail case:" + i + " should be " + solution[i] + " but is" + edge.debugPrint())
    }


}

fun getFact(nr: Int): Entity {
    return fact[EntityType.CurvedRail]!![2]!!.first { it.entityType == EntityType.AnySignal && it.direction == nr }
}
import com.google.gson.Gson
import factorioBlueprint.Entity
import factorioBlueprint.Position
import factorioBlueprint.ResultBP
import java.io.File
import kotlin.math.floor
import kotlin.math.round

fun main(args: Array<String>) {

    println("Program arguments: ${args.joinToString()}")


    val jsonString: String = File("test.json").readText(Charsets.UTF_8)
    val gson = Gson()
    var resultBP = gson.fromJson(jsonString, ResultBP::class.java)
    println(resultBP)


    //region filter relevant items and determinant min and max of BP
    val relevantEntity = arrayOf(
        "straight-rail",
        "rail-chain-signal",
        "rail-signal",
        "curved-rail"
    ) // ordered in guessed amount they appear in a BP
    var min = Position(0.0, 0.0)
    var max = Position(0.0, 0.0)
    var firstRelevant = true;
    val entityIter = resultBP.blueprint.entities.iterator()

    while (entityIter.hasNext()) {
        val entity = entityIter.next()

        if (!relevantEntity.contains(entity.name)) {
            entityIter.remove()
        }
        if (firstRelevant) {
            min = entity.position.copy()
            max = entity.position.copy()
            firstRelevant = false
        } else {
            val current = entity.position;
            if (min.x > current.x)
                min.x = current.x
            if (min.y > current.y)
                min.y = current.y
            if (max.x < current.x)
                max.x = current.x
            if (max.y < current.y)
                max.y = current.y
        }
    }
    //endregion
    //region transform coordinate space to start at 1, 1 (this makes the top left rail-corner be at 0.0)
    min.x = floor(min.x / 2) * 2
    min.y = floor(min.y / 2) * 2
    max.x -= min.x
    max.y -= min.y
    println(min)
    println(max)
    resultBP.blueprint.entities.forEach { entity ->
        entity.position.x -= min.x
        entity.position.y -= min.y
    }
    println(resultBP.blueprint.entities)

    var matrix: Array<Array<ArrayList<Entity>?>>
    matrix = Array((max.x - 1 / 2).toInt()) { row ->
        Array((max.y - 1 / 2).toInt()) { col ->
            null
        }
    }

    //endregion
    //region insert entity's into 2D Array for essayer look up in the next step
    resultBP.blueprint.entities.forEach { entity ->
        val x = floor(entity.position.x / 2).toInt()
        val y = floor(entity.position.y / 2).toInt()
        if (matrix[x][y] == null)
            matrix[x][y] = arrayListOf(entity)
        else
            matrix[x][y]!!.add(entity)
    }
    //endregion

}


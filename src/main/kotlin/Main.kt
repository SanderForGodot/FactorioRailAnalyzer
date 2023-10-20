import com.google.gson.Gson
import factorioBlueprint.Entity
import factorioBlueprint.Position
import factorioBlueprint.ResultBP
import java.io.File
import kotlin.contracts.contract
import kotlin.math.ceil
import kotlin.math.floor

fun main(args: Array<String>) {

    println("Program arguments: ${args.joinToString()}")


    val jsonString: String = File("bp.json").readText(Charsets.UTF_8)
    val gson = Gson()
    var resultBP = gson.fromJson(jsonString, ResultBP::class.java)
    println(resultBP)

    //todo: add signals to rail on l or r side
    //todo: have railssignals point with l or r to a rail
    //todo: update fact db for signals depending on l r cases

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
    //  println(resultBP.blueprint.entities)

    var matrix: Array<Array<ArrayList<Entity>?>>
    matrix = Array(ceil(max.x/ 2).toInt()) { row ->
        Array(ceil(max.y / 2).toInt()) { col ->
            null
        }
    }

    var signals : ArrayList<Entity> = arrayListOf();

    //endregion
    //region insert entity's into 2D Array for essayer look up in the next step
    resultBP.blueprint.entities.forEach { entity ->
        val x = floor(entity.position.x / 2).toInt()
        val y = floor(entity.position.y / 2).toInt()
        if (matrix[x][y] == null)
            matrix[x][y] = arrayListOf(entity)
        else
            matrix[x][y]!!.add(entity)
        Entity(0, "straight-rail", Position(0.0, -2.0), 0)
        Entity(0, "curved-rail", Position(3.0, -3.0), 5)


    }
    //endregion
    //region
    var listOfSignals: ArrayList<Entity> = arrayListOf();
    resultBP.blueprint.entities.forEach { entity ->
        if(entity.name =="rail-signal"||entity.name =="rail-chain-signal")
        {
            listOfSignals.add(entity)
            return@forEach
        }
        fact[entity.name]?.get(entity.direction)?.forEach { possibleRail ->
            var possiblePosition = entity.position + possibleRail.position
            val x = floor(possiblePosition.x / 2).toInt()
            val y = floor(possiblePosition.y / 2).toInt()
            if (x < 0 || y < 0) {
                return@forEach
            }
            matrix[x][y]?.forEach { foundRail ->
                if (foundRail.name.contains(possibleRail.name)
                    && foundRail.direction == possibleRail.direction) {
                    if (possibleRail.name == "signal")
                    {

                        if (foundRail.rightNextRail == null)
                            foundRail.rightNextRail = arrayListOf(entity)
                        else
                            foundRail.rightNextRail!!.add(entity)
                    }else {
                        if (possibleRail.entityNumber == 1) {//right
                            if (entity.rightNextRail == null)
                                entity.rightNextRail = arrayListOf(foundRail)
                            else
                                entity.rightNextRail!!.add(foundRail)
                        } else {
                            if (entity.leftNextRail == null)
                                entity.leftNextRail = arrayListOf(foundRail)
                            else
                                entity.leftNextRail!!.add(foundRail)
                        }
                    }
                }
            }
        }
    }

    listOfSignals.forEach{startPoint->
        startPoint.rightNextRail?.forEach {
            var edge: Edge= Edge(startPoint.name)
                edge.railList.add(it)
                 if (it.rightNextRail!=null && !it.rightNextRail?.contains(startPoint)!!)
                 {
                     // call recusion funcion  
                 }
        }

    }

    println(resultBP.blueprint.entities[0])
    println(resultBP.blueprint.entities[0].leftNextRail?.first())
    println(resultBP.blueprint.entities[0].leftNextRail?.first()?.leftNextRail?.first())
    println(resultBP.blueprint.entities[0].leftNextRail?.first()?.leftNextRail?.first()?.leftNextRail?.first())
    println(resultBP.blueprint.entities[0].leftNextRail?.first()?.leftNextRail?.first()?.leftNextRail?.first()?.leftNextRail?.first())

    /*
    "straight-rail",
        Direction
            0
              Top (Right)
                Entity(1,"straight-rail",Position(0.0,-2.0),0)
                Entity(1,"curved-rail", Position(-1.0,-5.0),0)
                Entity(1,"curved-rail", Position(1.0,-5.0),1)
              Bottum (left)
                Entity(-1,"straight-rail",Position(0.0, 2.0),0)
                Entity(-1,"curved-rail", Position(1.0,5.0),4)
                Entity(-1,"curved-rail", Position(-1.0,5.0),5)
            2
              Right
                Entity(1,"straight-rail",Position(2.0,0.0),2)
                Entity(1,"curved-rail", Position(5.0,-1.0),2)
                Entity(1,"curved-rail", Position(5.0,1.0),3)
              Left
                Entity(-1,"straight-rail",Position(-2.0,0.0),2)
                Entity(-1,"curved-rail", Position(-5.0,1.0),6)
                Entity(-1,"curved-rail", Position(-5.0,-1.0),7)
            1
              Right
                Entity(1,"straight-rail", Position(2.0,0.0),5)
                Entity(1,"curved-rail", Position(3.0,3.0),0)
              Left
                Entity(-1,"straight-rail", Position(0.0,-2.0),5)
                Entity(-1,"curved-rail", Position(-3.0,-3.0),3)

            5
              Right
                Entity(1,"straight-rail", Position(0.0, 2.0),1)
                Entity(1,"curved-rail", Position(3.0,3.0),7)
              Left
                Entity(-1,"straight-rail", Position(-2.0,0.0),1)
                Entity(-1,"curved-rail", Position(-3.0,-3.0),4)

            3
              Right
                Entity(1,"straight-rail", Position(2.0,0.0),7)
                Entity(1,"curved-rail", Position(3.0,-3.0),5)
              Left
                Entity(-1,"straight-rail", Position(0.0,2.0),7)
                Entity(-1,"curved-rail", Position(-3.0,3.0),2)

            7
              Right
                Entity(1,"straight-rail", Position(0.0,-2.0),3)
                Entity(1,"curved-rail", Position(3.0,-3.0),6)
              Left
                Entity(-1,"straight-rail", Position(-2.0,0.0),3)
                Entity(-1,"curved-rail", Position(-3.0,3.0),1)

    "rail-chain-signal",
    "rail-signal",
    "curved-rail"
        Direction
            0
              Right
                Entity(1,"straight-rail",Position(0.0, 2.0),0)
                Entity(1,"curved-rail", Position(1.0,5.0),4)
                Entity(1,"curved-rail", Position(-1.0,5.0),5)
              Left
                Entity(-1,"straight-rail", Position(-3.0,-3.0),1)
                Entity(-1,"curved-rail", Position(-4.0,-6.0),4)
            1
              Right
                Entity(1,"straight-rail", Position(3.0,-3.0),7)
                Entity(1,"curved-rail", Position(4.0,-6.0),5)
              Left
                Entity(-1,"straight-rail",Position(1.0, 5.0),0)
                Entity(-1,"curved-rail", Position(2.0,8.0),4)
                Entity(-1,"curved-rail", Position(0.0,8.0),5)
            2
              Right
                Entity(1,"straight-rail", Position(3.0,-3.0),3)
                Entity(1,"curved-rail", Position(6.0,-4.0),6)
              Left
                Entity(-1,"straight-rail",Position(-5.0,1.0),2)
                Entity(-1,"curved-rail", Position(-8.0,2.0),6)
                Entity(-1,"curved-rail", Position(-8.0,0.0),7)
            3
              Right
                Entity(1,"straight-rail", Position(3.0, 1.0),1)
                Entity(1,"curved-rail", Position(6.0,1.0),7)
              Left
                Entity(-1,"straight-rail",Position(-5.0,-1.0),2)
                Entity(-1,"curved-rail", Position(-8.0,0.0),6)
                Entity(-1,"curved-rail", Position(-8.0,-2.0),7)
            4
              Right
                Entity(1,"straight-rail", Position(3.0,3.0),5)
                Entity(1,"curved-rail", Position(4.0,6.0),0)
              Left
                Entity(1,"straight-rail",Position(-1.0,-5.0),0)
                Entity(1,"curved-rail", Position(-2.0,-8.0),0)
                Entity(1,"curved-rail", Position(0.0,-8.0),1)
            5
              Right
                Entity(1,"straight-rail",Position(1.0,-5.0),0)
                Entity(1,"curved-rail", Position(0.0,-8.0),0)
                Entity(1,"curved-rail", Position(2.0,-8.0),1)
              Left
                Entity(-1,"straight-rail", Position(-3.0,3.0),3)
                Entity(-1,"curved-rail", Position(-4.0,6.0),1)
            6
              Right
                Entity(1,"straight-rail",Position(5.0,-1.0),2)
                Entity(1,"curved-rail", Position(8.0,-2.0),2)
                Entity(1,"curved-rail", Position(8.0,0.0),3)
              Left
                Entity(-1,"straight-rail", Position(3.0,3.0),7)
                Entity(-1,"curved-rail", Position(-6.0,4.0),2)
            7
              Right
                Entity(1,"straight-rail",Position(5.0,1.0),2)
                Entity(1,"curved-rail", Position(8.0,0.0),2)
                Entity(1,"curved-rail", Position(8.0,2.0),3)
              Left
                Entity(-1,"straight-rail", Position(-3.0,-3.0),5)
                Entity(-1,"curved-rail", Position(-6.0,-4.0),3)




    * */

    //endregion

}





















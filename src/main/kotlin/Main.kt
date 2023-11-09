import com.google.gson.Gson
import factorioBlueprint.Entity
import java.util.*
import kotlin.collections.ArrayList

import factorioBlueprint.*
import java.io.ByteArrayOutputStream

import java.io.File

import java.util.zip.Inflater
import kotlin.math.ceil
import kotlin.math.floor


//some idea what this code does, but we didn't write it
//Made by @marcouberti on github
//Source: https://gist.github.com/marcouberti/40dbbd836562b35ace7fb2c627b0f34f
fun ByteArray.zlibDecompress(): String {
    val inflater = Inflater()
    val outputStream = ByteArrayOutputStream()

    return outputStream.use {
        val buffer = ByteArray(1024)

        inflater.setInput(this)

        var count = -1
        while (count != 0) {
            count = inflater.inflate(buffer)
            outputStream.write(buffer, 0, count)
        }

        inflater.end()
        outputStream.toString("UTF-8")
    }
}
fun decodeBpSting(filename:String):String{
    val base64String: String = File(filename).readText(Charsets.UTF_8)
    val decoded = Base64.getDecoder().decode(base64String.substring(1))
    val str : String = decoded.zlibDecompress()
    println(str)
    return str
}

fun main(args: Array<String>) {

    println("Program arguments: ${args.joinToString()}")




    val jsonString: String =  decodeBpSting("decodeTest.txt")
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
    //  println(resultBP.blueprint.entities)

    var matrix: Array<Array<ArrayList<Entity>?>>
    matrix = Array(ceil(max.x / 2).toInt()) { row ->
        Array(ceil(max.y / 2).toInt()) { col ->
            null
        }
    }

    var signals: ArrayList<Entity> = arrayListOf();

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
    //region rail Linker: connected rails point to each other with pointer list does the same with signals
    var listOfSignals: ArrayList<Entity> = arrayListOf();
    resultBP.blueprint.entities.forEach { entity ->
        if (entity.name == "rail-signal" || entity.name == "rail-chain-signal") {
            listOfSignals.add(entity)
            return@forEach
        }
        fact[entity.name]?.get(entity.direction)?.forEach { possibleRail ->
            var possiblePosition = entity.position + possibleRail.position
            val x = floor(possiblePosition.x / 2).toInt()
            val y = floor(possiblePosition.y / 2).toInt()
            if (x < 0 || y < 0 || matrix.size <= x || matrix[0].size <= y) {
                return@forEach
            }
            matrix[x][y]?.forEach { foundRail ->
                if (foundRail.name.contains(possibleRail.name)
                    && foundRail.direction == possibleRail.direction
                ) {
                    if (possibleRail.name == "signal") {
                        if (possibleRail.entityNumber == 1) {
                            foundRail.rightNextRail = createOrAdd(entity.rightNextRail, entity)
                            entity.signalOntheRight = createOrAdd(entity.rightNextRail, foundRail)
                        } else {
                            foundRail.leftNextRail = createOrAdd(entity.rightNextRail, entity)
                            entity.signalOntheLeft = createOrAdd(entity.rightNextRail, foundRail)
                        }
                    } else {
                        if (possibleRail.entityNumber == 1) {//right

                            entity.rightNextRail = createOrAdd(entity.rightNextRail, foundRail)

                        } else {

                            entity.leftNextRail = createOrAdd(entity.leftNextRail, foundRail)
                        }
                    }
                }
            }
        }
    }


    //endregion

    listOfSignals.forEach { startPoint ->
        startPoint.rightNextRail?.forEach {
            if (it.rightNextRail != null && !it.rightNextRail?.contains(startPoint)!!) {
                recusion(Edge(startPoint))
            }
        }
    }


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


}

fun recusion(edge: Edge) {
    var item = edge.EntityList.last()

    //  var corectdirection =

    leftBranch(item,edge)
    item.rightNextRail?.forEach { rightNext ->
        if (rightNext.leftNextRail!!.contains(item)) {
            //wrong way go oposite
        }
    }


}
fun leftBranch(item:Entity, edge: Edge){
    if(item.signalOntheRight != null)
    {
        //if rail on oposide left
            //end edge sucsesfuly
        //else
            //invalid edge
    }
    if(item.signalOntheLeft != null) {
    if (item.signalOntheLeft!!.size ==1)
    {
        // end edge sucsesfuly
    }else{
        // get closes 
    }

    }


    item.leftNextRail?.forEach { leftNext ->
        if (leftNext.rightNextRail!!.contains(item)) {
            var newEdge = Edge(edge)
            newEdge.EntityList.add(leftNext)
            recusion(newEdge)
        } else {

            //wenn das if einmal scheitert kann die ganze liste gespipt werden
            return;
        }
    }
}
fun rightBranch(){

}


fun <E> createOrAdd(_list: ArrayList<E>?, item: E): ArrayList<E> {
    var list = _list
    if (list == null)
        list = arrayListOf(item)
    else
        list.add(item)
    list.add(item)
    return list;
}



















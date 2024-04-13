package FRA

import EntityType
import addUnique
import fact
import factorioBlueprint.Entity
import factorioBlueprint.Position
import kotlin.math.ceil
import kotlin.math.floor

fun ArrayList<Entity>.determineMinMax(): Pair<Position, Position> {
    val min = this.first().position.copy()
    val max = min.copy()
    this.forEach { entity ->
        val current = entity.position
        if (min.x > current.x)
            min.x = current.x
        if (min.y > current.y)
            min.y = current.y
        if (max.x < current.x)
            max.x = current.x
        if (max.y < current.y)
            max.y = current.y
    }
    // round down min value to be certain that every rail is included
    min.x = floor(min.x / 2) * 2
    min.y = floor(min.y / 2) * 2

    return Pair(min, max)
}

fun generateMatrix(size: Position): Array<Array<ArrayList<Entity>?>> {
    // the coordinate space is compress by 2 to reduce the amount of empty List, as the Rails are on a 2 by 2 coordinate space anyway
    return Array(ceil(size.x / 2).toInt() + 1) {
        Array(ceil(size.y / 2).toInt() + 1) {
            null
        }
    }
}

fun ArrayList<Entity>.filedMatrix(size: Position): Array<Array<ArrayList<Entity>?>> {
    if (size.x < 8) {
        size.x = 8.0
    }// make size at least 8 big, so that the matrix is at least 4 big, since a curved rail has the position 4
    if (size.y < 8) {
        size.y = 8.0
    }
    val matrix = generateMatrix(size)
    // insert entity's into 2D Array based on the x y coordinates of the entity
    this.forEach { entity ->
        entity.ini()
        // calculate target x y base on the squashed system
        val x = floor(entity.position.x / 2).toInt()
        val y = floor(entity.position.y / 2).toInt()
        if (matrix[x][y] == null)
            matrix[x][y] = arrayListOf(entity)
        else
            matrix[x][y]!!.add(entity)
    }
    return matrix
}


fun ArrayList<Entity>.railLinker(matrix: Array<Array<ArrayList<Entity>?>>) {
    //endregion
    this.filter { entity ->
        entity.isRail()
    }.forEach outer@{ rail -> // for Each (R)eal rail entity
        fact[rail.entityType]?.get(rail.direction)?.forEach inner@{ factEntity -> // for each entity T
            // calculate P = R + T
            val possiblePosition = rail.position + factEntity.position
            val x = floor(possiblePosition.x / 2).toInt()
            val y = floor(possiblePosition.y / 2).toInt()
            if (x < 0 || y < 0 || matrix.size <= x || matrix[0].size <= y) {
                return@inner
            }
            //look up P in matrix
            addEachMatchingEntity(matrix[x][y], factEntity, rail)
        }
    }
}

fun addEachMatchingEntity(entities: ArrayList<Entity>?, factEntity: Entity, rail: Entity) {
    entities?.filter { entity ->// for each existing Entity E
        // if T and E are equal (except position)
        ((entity.isSignal() && factEntity.isSignal()) ||
                entity.entityType == factEntity.entityType)
                && entity.direction == factEntity.direction
    }?.forEach { foundEntity ->
        // yes -> Add a reference from R to E to the direction depending on T
        if (foundEntity.isSignal()) {
            // for signal, we do tow way add and set an edge case var
            foundEntity.removeRelatedRail = setRemoveRelatedRail(foundEntity, factEntity, rail)
            foundEntity.getRailList(factEntity.entityNumber!!).addUnique(rail)
            rail.getSignalList(factEntity.entityNumber!!).addUnique(foundEntity)
        } else {
            rail.getRailList(factEntity.entityNumber!!).addUnique(foundEntity)
        }
    }
}

fun setRemoveRelatedRail(foundRail: Entity, factEntity: Entity, rail: Entity): Boolean {
    //set removeRelatedRail depending on foundRail (A) and possibleRail (input)
    val current = foundRail.removeRelatedRail
    val factVal = factEntity.removeRelatedRail!! //theoretical value determined by the fact.tk
    return when (current) {
        // if bool is not set take the input value
        null -> factVal
        // if the bool and the input have the same value all ok continue
        factVal -> current
        // if the rails disagree prioritise the curved rail state
        else -> (rail.entityType == EntityType.CurvedRail) == factVal

    }
}
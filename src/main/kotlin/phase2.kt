import factorioBlueprint.Entity
import kotlin.math.floor

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
        (entity.isSignal() == factEntity.isSignal() ||
                entity.isRail() == factEntity.isRail())  //technically  this check is unnecessary
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
        (factVal == current) -> current
        // if the rails disagree prioritise the curved rail state
        else -> (rail.entityType == EntityType.CurvedRail) == factVal

    }
}
package FRA

import Clases.Block
import Clases.Edge
import Clases.EntityType
import factorioBlueprint.Entity
import java.util.*

fun Graph<Block>.analysis(): Graph<Block> {
    val markedForRemove: MutableList<List<Block>> = mutableListOf()
    this.circularDependencies.forEach {
        val result = it.analysis()
        if (result == null)
            markedForRemove.add(it)
        else
            this.dangerSigs.addAll(result)

    }
    return this
}


private fun <E> List<E>.at(i: Int): E {
    return this[((i % this.size) + this.size) % this.size]
}

private fun List<Block>.analysis(): List<Entity>? {
    val transSetList = ArrayList<Set<Edge>>()
    val indexList = ArrayList<Int>()
    for (i in indices) {
        val r = this.at(i).getEdge(this.at(i - 1), this.at(i + 1))
        transSetList.add(r)
        if (r.size == 2)
            indexList.add(i)
    }
    val transList = transSetList.flatten()
    if (indexList.size == 0)
        //this is one big circle
        return transList.map { it.entityList.first() }
    Collections.rotate(transList, -1 * indexList[0])
    for (i in indexList.indices)
        indexList[i] = i + indexList[i] - indexList[0]

    for (i in 0 until indexList.size - 1)
        if (transList.subList(indexList[i], indexList[i + 1]).all {
                it.entityList.first().entityType != EntityType.Signal
            })
            return null
    //return true

    return (0 until indexList.size - 1)
        .flatMap { i ->
            transList.subList(indexList[i], indexList[i + 1]).filter {
                it.entityList.first().entityType == EntityType.Signal
            }.map { it ->
                it.entityList.first()

            }
        }


}

fun Block.getEdge(form: Block, to: Block): Set<Edge> {

    val firstOptions = this.edgeList.filter { edge ->
        form.edgeList.mapNotNull { it.nextEdgeList }.flatten()
            .contains(edge)
    }
    val secondOptions = this.edgeList.filter { edge ->

        edge.nextEdgeList?.mapNotNull { it.belongsToBlock }
            ?.contains(to) == true
    }
    val intersect = firstOptions intersect secondOptions.toSet()
    var union = firstOptions union secondOptions
    if (intersect.size == 1) return intersect
    if (union.size == 2) return union
    val a = firstOptions.toMutableList()
    a.retainAll { edge ->
        secondOptions.any {
            edge.doesCollide(it)
        }
    }
    val b = secondOptions.toMutableList()
    b.retainAll { edge ->
        firstOptions.any {
            edge.doesCollide(it)
        }
    }
    union = a union b
    if (union.size == 2) return union
    throw Exception("Not yet implemented: 2 ways to get from block a to block b")
}


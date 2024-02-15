import factorioBlueprint.Entity

val a = 0;
val b = 1;
val c = 2;
val d = 3;
fun determineEndingSander(edge: Edge, direction: Int): Edge? {
    val goodSide = edge.last(1).getSignalList(direction)
    val wrongSide = edge.last(1).getSignalList(-direction)

    // var namen relativ zu R2 einer gebogenen schiene (grafik)
    var untenLinks: Entity? = retiveSignal(goodSide, false)
    var untenRechts: Entity? = retiveSignal(goodSide, true)
    var obenLinks: Entity? = retiveSignal(wrongSide, true)
    var obenRechts: Entity? = retiveSignal(wrongSide, false)
    var startSignal = edge.EntityList.first()
    if (goodSide.contains(startSignal)) {
        var a = obenLinks
        var b = obenRechts
        var x = untenLinks
        var y = untenRechts
        var c = goodSide.size == 2
        var d = x == startSignal

        var abcd = arrayListOf<Any?>(a, b, c, d)

        var endingSig = topCase(a, b, c, d, y)
        if (endingSig == null)
            return null
        var validRail = goodSide.contains(endingSig)

        return edge.finishUpEdge(endingSig, validRail)
    } else {
        var abcd = arrayListOf<Entity?>(obenLinks, untenLinks, obenRechts, untenRechts)
        var endingSig = bottomCase(abcd)
        var validRail = goodSide.contains(endingSig)
        return edge.finishUpEdge(endingSig, validRail)

    }


}

fun topCase(a: Entity?, b: Entity?, c: Boolean, d: Boolean, y: Entity?): Entity? {
    if (!d)
        return null
    if (c)
        return y
    return b
}

fun bottomCase(abcd: ArrayList<Entity?>): Entity {
    if (abcd[b] != null)
        return abcd[b]!!
    if (abcd[a] != null)
        return abcd[a]!!
    if (abcd[d] != null)
        return abcd[d]!!
    if (abcd[c] != null)
        return abcd[c]!!
    throw Exception("Determine ending should have never been called!")
}

fun retiveSignal(signalList: ArrayList<Entity>, b: Boolean): Entity? {
    return if (signalList.size == 0)
        null
    else
        if (signalList[0].removeRelatedRail == b)
            signalList[0]
        else
            if (signalList.size == 1)
                null
            else // signalList.size == 2
                signalList[1]
}
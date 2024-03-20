import factorioBlueprint.Position

interface Grafabel : Comparable<Grafabel> {
    fun uniqueID(): Int
    fun pos(): Position
    fun hasRailSignal():Boolean
    override fun compareTo(other: Grafabel): Int {
        return uniqueID() - other.uniqueID()
    }


}
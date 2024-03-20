import factorioBlueprint.Position

interface Grafabel : Comparable<Grafabel> {
    fun uniqueID(): Int
    fun pos(): Position
    override fun compareTo(other: Grafabel): Int {
        return uniqueID() - other.uniqueID()
    }

    fun label(): String {
        return "id:${uniqueID()}"
    }


}
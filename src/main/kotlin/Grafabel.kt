interface Grafabel:Comparable<Grafabel> {



    fun uniqueID():Int
    fun hasNextOptions():Boolean
    override fun compareTo(other: Grafabel): Int {
        return uniqueID()-other.uniqueID()
    }
}
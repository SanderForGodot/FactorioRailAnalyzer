package factorioBlueprint

import com.google.gson.annotations.SerializedName


data class Entity(

    @SerializedName("entity_number") var entityNumber: Int? = null,
    @SerializedName("name") var name: String,
    @SerializedName("position") var position: Position = Position(0.0, 0.0),
    @SerializedName("direction") var direction: Int = 0,  // when no direction is provided it is in the default aka 0 direction
    var removeRelatedRail: Boolean?=null

) {
    lateinit var leftNextRail: ArrayList<Entity>  // also reused for signals to reference a conected rail
    lateinit var rightNextRail: ArrayList<Entity>
    lateinit var signalOntheLeft: ArrayList<Entity>
    lateinit var signalOntheRight: ArrayList<Entity>

    fun ini() {
        leftNextRail = arrayListOf()
        rightNextRail = arrayListOf()
        signalOntheLeft = arrayListOf()
        signalOntheRight = arrayListOf()
    }

    override fun equals(other: Any?): Boolean {
        if (other !is Entity)
            return false
        val other: Entity = other as Entity

        return position == other.position
                && name == other.name
                && direction == other.direction
    }

    fun getDirectionalRailList(direction: Int): ArrayList<Entity> {
        return if (direction == -1)
            leftNextRail
        else
            rightNextRail
    }

    fun getDirectionalSignalList(direction: Int): ArrayList<Entity>? {
        if (direction == -1)
            return signalOntheLeft;
        else if (direction == 1)
            return signalOntheRight
        else
            return null
    }

    fun hasSignal(): Boolean {
        return signalOntheLeft.size + signalOntheRight.size > 0
    }

    fun getTheSingleRail(): ArrayList<Entity> {
        val arr: ArrayList<Entity> = arrayListOf<Entity>()
        rightNextRail.let { arr.addAll(it) }
        leftNextRail.let { arr.addAll(it) }
        assert(arr.size == 1)
        return arr;
    }
    fun distanceTo(entity: Entity): Double {
        return this.position.distanceTo(entity.position);
    }

    fun relevantShit(): String {

        return "Entity(entityNumber=$entityNumber, name='$name', position=$position, direction=$direction, leftNextRail=$leftNextRail, rightNextRail=$rightNextRail, signalOntheLeft=$signalOntheLeft, signalOntheRight=$signalOntheRight)"
    }

    override fun toString(): String {
        return "Entity(entityNumber=$entityNumber)"
    }


}
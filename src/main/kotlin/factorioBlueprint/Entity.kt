package factorioBlueprint


import EntityType
import com.google.gson.annotations.SerializedName


data class Entity(

    // we re use the entityNumber as an indication for direction wich we only do in the fakc.kt
    @SerializedName("entity_number") var entityNumber: Int? = null,
    @SerializedName("name") var entityType: EntityType = EntityType.Error,
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
                && entityType == other.entityType
                && direction == other.direction
    }

    fun getRailList(direction: Int): ArrayList<Entity> {
        return if (direction == -1)
            leftNextRail
        else if (direction == 1)
            rightNextRail
        else
            throw Exception("getRailList expects -1 or 1 ")
    }

    fun getSignalList(direction: Int): ArrayList<Entity> {
        if (direction == -1)
            return signalOntheLeft;
        else if (direction == 1)
            return signalOntheRight
        else
          throw Exception("getSignalList expects -1 or 1 ")
    }

    fun hasSignal(): Boolean {
        return signalOntheLeft.size + signalOntheRight.size > 0
    }

    fun distanceTo(entity: Entity): Double {
        return this.position.distanceTo(entity.position);
    }

    fun relevantShit(): String {

        return "Entity(entityNumber=$entityNumber, name='${entityType.name}', position=$position, direction=$direction, leftNextRail=$leftNextRail, rightNextRail=$rightNextRail, signalOntheLeft=$signalOntheLeft, signalOntheRight=$signalOntheRight)"
    }

    fun etityNumberString(): String {
        return "Entity(entityNumber=$entityNumber)"
    }
    override fun toString(): String {
        return "Entity(entityType=$entityType, position=$position, direction=$direction)"
    }

    fun isSignal():Boolean{
        return entityType.isSignal()
    }

    fun isRail():Boolean{
        return entityType.isRail()
    }



}
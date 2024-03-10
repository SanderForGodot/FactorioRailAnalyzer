package factorioBlueprint


import EntityType
import com.google.gson.annotations.SerializedName


data class Entity(

    // we re-use the entityNumber as an indication for direction which we only do in the fact.kt
    @SerializedName("entity_number") var entityNumber: Int? = null,
    @SerializedName("name") var entityType: EntityType = EntityType.Error,//this is overwritten by the GSON and will be set to null when none is found
    @SerializedName("position") var position: Position = Position(0.0, 0.0),
    @SerializedName("direction") var direction: Int = 0,  // when no direction is provided it is in the default aka 0 direction
    var removeRelatedRail: Boolean? = null

) {

    lateinit var leftNextRail: ArrayList<Entity>  // also reused for signals to reference a connected rail
    lateinit var rightNextRail: ArrayList<Entity>
    lateinit var signalOnTheLeft: ArrayList<Entity>
    lateinit var signalOnTheRight: ArrayList<Entity>
    fun ini() {
        leftNextRail = arrayListOf()
        rightNextRail = arrayListOf()
        signalOnTheLeft = arrayListOf()
        signalOnTheRight = arrayListOf()
    }

    override fun equals(other: Any?): Boolean {
        if (other !is Entity)
            return false
        @Suppress("NAME_SHADOWING") val other: Entity = other

        val isSameEntityType: Boolean =
            if (this.entityType == EntityType.AnySignal || other.entityType == EntityType.AnySignal)
                (this.isSignal() && other.isSignal())
            else
                entityType == other.entityType

        return position == other.position
                && isSameEntityType
                && direction == other.direction
    }

    fun getRailList(direction: Int): ArrayList<Entity> {
        return when (direction) {
            -1 -> leftNextRail
            1 -> rightNextRail
            else -> throw Exception("getRailList expects -1 or 1 ")
        }
    }

    fun getSignalList(direction: Int): ArrayList<Entity> {
        return when (direction) {
            -1 -> signalOnTheLeft
            1 -> signalOnTheRight
            else -> throw Exception("getSignalList expects -1 or 1 ")
        }
    }

    fun hasSignal(): Boolean {
        return signalOnTheLeft.size + signalOnTheRight.size > 0
    }

    fun signalCount(): Int {
        return signalOnTheLeft.size + signalOnTheRight.size
    }

    fun toMannySignals(): Boolean {
        return signalCount() > 2 && entityType == EntityType.Rail
    }

    fun distanceTo(entity: Entity): Double {
        return this.position.distanceTo(entity.position)
    }

    fun relevantShit(): String {

        return "Entity(entityNumber=$entityNumber, name='${entityType.name}', position=$position, direction=$direction, leftNextRail=$leftNextRail, rightNextRail=$rightNextRail, signalOnTheLeft=$signalOnTheLeft, signalOnTheRight=$signalOnTheRight)"
    }

    override fun toString(): String {
        return "Entity(entityType=$entityType, position=$position, direction=$direction)"
    }

    fun isSignal(): Boolean {
        return entityType.isSignal()
    }

    fun isRail(): Boolean {
        return entityType.isRail()
    }


}
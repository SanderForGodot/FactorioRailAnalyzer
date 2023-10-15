package factorioBlueprint

import com.google.gson.annotations.SerializedName


data class Entity (

  @SerializedName("entity_number" ) var entityNumber : Int?      = null,
  @SerializedName("name"          ) var name         : String   ,
  @SerializedName("position"      ) var position     : Position = Position(0.0,0.0),
  @SerializedName("direction"     ) var direction    : Int      = 0  // when no direction is provided it is in the default aka 0 direction

){

  var leftNextRail: ArrayList<Entity>? =null
  var rightNextRail:ArrayList<Entity>? = null
  lateinit var railThatBelongsToSignal: Entity

  override fun equals(other: Any?): Boolean {
    if(other !is Entity)
      return false;
    val other: Entity = other as Entity

    return position == other.position
            && name == other.name
            && direction == other.direction
  }
}
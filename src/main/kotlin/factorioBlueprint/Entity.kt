package factorioBlueprint

import com.google.gson.annotations.SerializedName


data class Entity (

  @SerializedName("entity_number" ) var entityNumber : Int?      = null,
  @SerializedName("name"          ) var name         : String   ,
  @SerializedName("position"      ) var position     : Position = Position(0.0,0.0),
  @SerializedName("direction"     ) var direction    : Int      = 0  // when no direction is provided it is in the default aka 0 direction

){

  var leftNextRail: ArrayList<Entity>? =null // also reused for signals to reference a conected rail
  var rightNextRail:ArrayList<Entity>? = null
  var signalOntheLeft : ArrayList<Entity>? = null
  var signalOntheRight : ArrayList<Entity>? = null


  override fun equals(other: Any?): Boolean {
    if(other !is Entity)
      return false;
    val other: Entity = other as Entity

    return position == other.position
            && name == other.name
            && direction == other.direction
  }

  fun getDirectionalRailList(direction: Int):ArrayList<Entity>?{
    if (direction ==-1)
      return leftNextRail;
    else if (direction==1)
      return rightNextRail
    else
      return null
  }
  fun getDirectionalSignalList(direction: Int):ArrayList<Entity>?{
    if (direction ==-1)
      return signalOntheLeft;
    else if (direction==1)
      return signalOntheRight
    else
      return null
  }

  fun hasSignal():Boolean
  {
    return signalOntheLeft!= null || signalOntheRight != null
  }

}
package factorioBlueprint

import com.google.gson.annotations.SerializedName


data class Entity (

  @SerializedName("entity_number" ) var entityNumber : Int?      = null,
  @SerializedName("name"          ) var name         : String   ,
  @SerializedName("position"      ) var position     : Position? = Position(0.0,0.0),
  @SerializedName("direction"     ) var direction    : Int      = 0  // when no direction is provided it is in the default aka 0 direction

)
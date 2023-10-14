package factorioBlueprint

import com.google.gson.annotations.SerializedName


data class Entities (

  @SerializedName("entity_number" ) var entityNumber : Int?      = null,
  @SerializedName("name"          ) var name         : String?   = null,
  @SerializedName("position"      ) var position     : Position? = Position(),
  @SerializedName("direction"     ) var direction    : Int?      = null

)
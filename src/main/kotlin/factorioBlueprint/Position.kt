package factorioBlueprint

import com.google.gson.annotations.SerializedName


data class Position (

  @SerializedName("x" ) var x : Double? = null,
  @SerializedName("y" ) var y : Double? = null

)
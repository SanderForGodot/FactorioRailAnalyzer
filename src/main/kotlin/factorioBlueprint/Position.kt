package factorioBlueprint

import com.google.gson.annotations.SerializedName


data class Position (

  @SerializedName("x" ) var x : Double ,
  @SerializedName("y" ) var y : Double

)
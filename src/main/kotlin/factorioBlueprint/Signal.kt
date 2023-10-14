package factorioBlueprint

import com.google.gson.annotations.SerializedName


data class Signal (

  @SerializedName("type" ) var type : String? = null,
  @SerializedName("name" ) var name : String? = null

)
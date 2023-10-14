package factorioBlueprint

import com.google.gson.annotations.SerializedName


data class Icons (

  @SerializedName("signal" ) var signal : Signal? = Signal(),
  @SerializedName("index"  ) var index  : Int?    = null

)
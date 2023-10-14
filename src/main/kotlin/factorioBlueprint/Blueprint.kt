package factorioBlueprint

import com.google.gson.annotations.SerializedName



data class Blueprint (

  @SerializedName("icons"    ) var icons    : ArrayList<Icons>    = arrayListOf(),
  @SerializedName("entities" ) var entities : ArrayList<Entities> = arrayListOf(),
  @SerializedName("item"     ) var item     : String?             = null,
  @SerializedName("version"  ) var version  : Long?                = null

)
package factorioBlueprint

import com.google.gson.annotations.SerializedName



data class ResultBP (

  @SerializedName("blueprint" ) var blueprint : Blueprint? = Blueprint()

)
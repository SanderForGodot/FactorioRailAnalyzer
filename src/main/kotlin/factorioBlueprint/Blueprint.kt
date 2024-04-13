package factorioBlueprint

import com.google.gson.annotations.SerializedName


data class Blueprint(
    @SerializedName("entities") var entities: ArrayList<Entity> = arrayListOf(),
)
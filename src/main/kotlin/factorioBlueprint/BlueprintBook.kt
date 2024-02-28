package factorioBlueprint
import com.google.gson.annotations.SerializedName

class BlueprintBook {
    @SerializedName("blueprint_book" ) var blueprint_book : BlueprintList = BlueprintList()

}

class BlueprintList{
    @SerializedName("blueprints" ) var blueprints : List<ResultBP> = listOf(ResultBP())

}


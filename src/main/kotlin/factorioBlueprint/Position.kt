package factorioBlueprint

import com.google.gson.annotations.SerializedName
import java.util.Properties
import kotlin.math.hypot


data class Position (

  @SerializedName("x" ) var x : Double ,
  @SerializedName("y" ) var y : Double

) {
  operator fun plus(position: Position): Position {
   return Position(
      x+ position.x,
     y+ position.y
    )
  }
    override fun equals(other: Any?): Boolean {
        if(other !is Position)
            return false;
        val other: Position = other as Position

        return x == other.x && y == other.y;
    }
}
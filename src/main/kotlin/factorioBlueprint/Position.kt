package factorioBlueprint

import com.google.gson.annotations.SerializedName
import kotlin.math.pow
import kotlin.math.sqrt


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
    fun distanceTo(position:Position ): Double {
        val yDifference = (position.y - this.y).pow(2)
        val xDifference = (position.x - this.x).pow(2)
        return sqrt((yDifference + xDifference))
    }

    override fun equals(other: Any?): Boolean {
        if(other !is Position)
            return false;
        val other: Position = other as Position

        return x == other.x && y == other.y;
    }
}
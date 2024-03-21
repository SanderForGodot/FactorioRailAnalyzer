package factorioBlueprint

import com.google.gson.annotations.SerializedName
import kotlin.math.pow
import kotlin.math.round
import kotlin.math.sqrt


data class Position(

    @SerializedName("x") var x: Double,
    @SerializedName("y") var y: Double

) {
    fun distanceTo(position: Position): Double {
        val yDifference = (position.y - this.y).pow(2)
        val xDifference = (position.x - this.x).pow(2)
        return sqrt((yDifference + xDifference))
    }

    //region Math functions
    operator fun plus(position: Position): Position {
        return Position(
            x + position.x,
            y + position.y
        )
    }

    operator fun minus(position: Position): Position {
        return Position(
            x - position.x,
            y - position.y
        )
    }

    operator fun minusAssign(other: Position) {
        this.x -= other.x
        this.y -= other.y
    }

    operator fun div(dividend: Int): Position {
        this.x /= dividend
        this.y /= dividend
        return this
    }

    operator fun times(i: Int): Position {
        this.x *= i
        this.y *= i
        return this
    }

    fun round(): Position {
        return Position(round(x), round(y))
    }

    //endregion
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Position

        return (x == other.x) && (y == other.y)
    }

    override fun hashCode(): Int {
        var result = x.hashCode()
        result = 31 * result + y.hashCode()
        return result
    }

    override fun toString(): String {
        return "($x,$y)"
    }
}
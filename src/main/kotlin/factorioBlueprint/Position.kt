package factorioBlueprint

import com.google.gson.annotations.SerializedName
import kotlin.math.pow
import kotlin.math.sqrt


data class Position(

    @SerializedName("x") var x: Double,
    @SerializedName("y") var y: Double

) {
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

    fun distanceTo(position: Position): Double {
        val yDifference = (position.y - this.y).pow(2)
        val xDifference = (position.x - this.x).pow(2)
        return sqrt((yDifference + xDifference))
    }

    override fun equals(other: Any?): Boolean {
        if (other !is Position)
            return false
        @Suppress("NAME_SHADOWING") val other: Position = other
        return x == other.x && y == other.y
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

    override fun toString(): String {
        return "($x,-$y)"
    }
}
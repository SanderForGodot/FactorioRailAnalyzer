package test

import factorioBlueprint.Position
import intersect

fun main() {
    val a = Position(1.0, 0.1)
    val b = Position(0.0, -1.0)
    val c = Position(0.0, 1.0)
    val d = Position(2.0, -1.0)

   println( intersect(a,b,c,d))

}